package uk.ac.tees.mad.quicklist.presentation.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.ac.tees.mad.quicklist.data.local.GetTask
import uk.ac.tees.mad.quicklist.data.local.PostTask
import uk.ac.tees.mad.quicklist.data.local.TaskDao
import uk.ac.tees.mad.quicklist.data.local.TaskEntity
import uk.ac.tees.mad.quicklist.data.remote.api.activityDto.ActivityDtoItem
import uk.ac.tees.mad.quicklist.domain.reposiotry.BoredRepository
import uk.ac.tees.mad.quicklist.notification.NotificationScheduler
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val repository: BoredRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _addEditState = MutableStateFlow(AddEditUiState())
    val addEditState: StateFlow<AddEditUiState> = _addEditState

    fun onTitleChanged(value: String) {
        _addEditState.value = _addEditState.value.copy(title = value)
    }

    fun onNotesChanged(value: String) {
        _addEditState.value = _addEditState.value.copy(notes = value)
    }

    fun onPriorityChanged(value: String) {
        _addEditState.value = _addEditState.value.copy(priority = value)
    }

    fun onDueDateChanged(value: Long) {
        _addEditState.value = _addEditState.value.copy(dueDate = value)
    }

    fun onImageCaptured(imagePath: String?) {
        _addEditState.value = _addEditState.value.copy(imageUri = imagePath)
    }

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _getTask = MutableStateFlow<List<TaskEntity>>(emptyList())
    val getTask: StateFlow<List<TaskEntity>> = _getTask

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dzsqn6pd5",
            "api_key" to "653551969187332",
            "api_secret" to "L5E_fZo69-9wXCf0WJRYS827FYg"
        )
    )

    fun uploadImage(filePath: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = cloudinary.uploader().upload(
                    filePath,
                    mapOf("folder" to "quicklist/tasks")
                )
                withContext(Dispatchers.Main) {
                    onResult(true, result["secure_url"] as String)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, e.message)
                }
            }
        }
    }

    fun fetchTasks() {
        auth.currentUser?.uid?.let { userId ->
            db.collection(userId).get().addOnSuccessListener { snapshot ->
                viewModelScope.launch(Dispatchers.IO) {
                    snapshot.documents.forEach { doc ->
                        val task = doc.toObject(GetTask::class.java) ?: return@forEach
                        val dueDate = when (val v = doc.get("dueDate")) {
                            is Long -> v
                            is String -> v.toLongOrNull() ?: 0L
                            else -> 0L
                        }

                        taskDao.upsert(
                            TaskEntity(
                                id = task.id,
                                title = task.title,
                                description = task.description,
                                notes = task.notes,
                                priority = task.priority,
                                dueDate = dueDate,
                                imageUri = task.imageUri,
                                completed = task.completed,
                                timestamp = task.timestamp,
                                userId = task.userId
                            )
                        )
                    }
                    _getTask.value = taskDao.getAllTasks()
                }
            }
        }
    }

    fun deleteTaskLocally(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.deleteTaskById(taskId)
            NotificationScheduler.cancelNotification(context, taskId)
            _getTask.value = taskDao.getAllTasks()
        }
    }

    fun deleteTaskFromFirestore(taskId: String, onResult: (Boolean, String) -> Unit) {
        auth.currentUser?.uid?.let { userId ->
            db.collection(userId).document(taskId).delete()
                .addOnSuccessListener {
                    NotificationScheduler.cancelNotification(context, taskId)
                    onResult(true, "Task deleted")
                }
                .addOnFailureListener {
                    onResult(false, it.message ?: "Error")
                }
        }
    }

    fun toggleTaskCompletion(
        taskId: String,
        currentStatus: Boolean,
        onResult: (Boolean, String) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return
        db.collection(userId).document(taskId)
            .update("completed", !currentStatus)
            .addOnSuccessListener { onResult(true, "Updated") }
            .addOnFailureListener { onResult(false, it.message ?: "Failed") }
    }

    fun updateTaskInFirestore(
        taskId: String,
        title: String,
        description: String,
        notes: String,
        priority: String,
        dueDate: Long,
        imageUri: String?,
        onResult: (Boolean, String) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return
        val updates = mapOf(
            "title" to title,
            "description" to description,
            "notes" to notes,
            "priority" to priority,
            "dueDate" to dueDate,
            "imageUri" to imageUri
        )

        db.collection(userId).document(taskId)
            .update(updates)
            .addOnSuccessListener {
                NotificationScheduler.cancelNotification(context, taskId)
                NotificationScheduler.scheduleNotification(context, taskId, title, notes, dueDate)
                onResult(true, "Task updated")
            }
            .addOnFailureListener {
                onResult(false, it.message ?: "Update failed")
            }
    }

    fun saveItem(onDone: (Boolean, String) -> Unit) {
        val state = _addEditState.value
        val timestamp = System.currentTimeMillis()

        if (state.title.isEmpty()) {
            onDone(false, "Title is required")
            return
        }

        if (state.dueDate == 0L) {
            onDone(false, "Please select a date and time")
            return
        }

        auth.currentUser?.uid?.let { userId ->
            val ref = db.collection(userId).document()
            val id = ref.id

            val task = PostTask(
                id = id,
                title = state.title,
                description = state.notes,
                notes = state.notes,
                priority = state.priority,
                dueDate = state.dueDate,
                imageUri = state.imageUri,
                completed = false,
                timestamp = timestamp,
                userId = userId
            )

            ref.set(task)
                .addOnSuccessListener {
                    NotificationScheduler.scheduleNotification(
                        context,
                        id,
                        state.title,
                        state.notes,
                        state.dueDate
                    )

                    viewModelScope.launch(Dispatchers.IO) {
                        taskDao.upsert(
                            TaskEntity(
                                id = id,
                                title = state.title,
                                description = state.notes,
                                notes = state.notes,
                                priority = state.priority,
                                dueDate = state.dueDate,
                                imageUri = state.imageUri,
                                completed = false,
                                timestamp = timestamp,
                                userId = userId
                            )
                        )
                        _getTask.value = taskDao.getAllTasks()
                    }

                    onDone(true, "Saved successfully")
                }
                .addOnFailureListener {
                    onDone(false, it.message ?: "Save failed")
                }
        } ?: onDone(false, "User not authenticated")
    }

    private val _activity = MutableStateFlow<List<ActivityDtoItem>?>(null)
    val activity: StateFlow<List<ActivityDtoItem>?> = _activity

    fun loadActivityByType(type: String) {
        viewModelScope.launch {
            try {
                _activity.value = repository.getRandomActivity(type)
            } catch (e: Exception) {
                Log.e("BoredViewModel", e.message ?: "")
            }
        }
    }
}

data class AddEditUiState(
    val title: String = "",
    val notes: String = "",
    val priority: String = "Normal",
    val dueDate: Long = 0L,
    val imageUri: String? = null
)

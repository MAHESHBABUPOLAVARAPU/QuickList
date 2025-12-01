package uk.ac.tees.mad.quicklist.presentation.ViewModel

import android.R.attr.description
import android.util.Log
import android.util.Log.e
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable.isCompleted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.quicklist.data.local.GetTask
import uk.ac.tees.mad.quicklist.data.local.PostTask
import uk.ac.tees.mad.quicklist.data.local.TaskDao
import uk.ac.tees.mad.quicklist.data.local.TaskEntity
import uk.ac.tees.mad.quicklist.data.remote.api.activityDto.ActivityDtoItem
import uk.ac.tees.mad.quicklist.domain.reposiotry.BoredRepository
import uk.ac.tees.mad.safeher.presentation.ViewModel.GetUserInfo
import java.util.StringTokenizer
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor( private val taskDao: TaskDao ,private val repository: BoredRepository) : ViewModel() {
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

    fun onDueDateChanged(value: String) {
        _addEditState.value = _addEditState.value.copy(dueDate = value)
    }

    fun onImageCaptured(uri: String?) {
        _addEditState.value = _addEditState.value.copy(imageUri = uri)
    }

    val db = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _getTask = MutableStateFlow<List<TaskEntity>>(emptyList())
    val getTask: StateFlow<List<TaskEntity>> = _getTask


//    fun fetchCurrentUserData() {
//        auth.currentUser?.uid?.let { userId ->
//
//            db.collection("user").document(userId).addSnapshotListener { snapshot, e ->
//
//                if (e != null) {
//
//                    return@addSnapshotListener
//                }
//
//                if (snapshot != null && snapshot.exists()) {
//                    val data = snapshot.toObject(GetUserInfo::class.java)
//                    data?.let {
//                        _currentUserData.value = it
//                        Log.d("Firestore","$it")
//                    }
//                }
//            }
//        }
//    }


    fun deleteTaskLocally(taskId: String) {
        viewModelScope.launch {
            taskDao.deleteTaskById(taskId)
            _getTask.value = taskDao.getAllTasks()
        }
    }

    fun fetchTasks() {
        auth.currentUser?.uid?.let { userId ->

            db.collection(userId)
                .get()
                .addOnSuccessListener { snapshot ->

                    val tasks = snapshot.toObjects(GetTask::class.java)

                    viewModelScope.launch {
                        tasks.forEach { task ->

                            taskDao.upsert(
                                TaskEntity(
                                    id = task.id,
                                    title = task.title,
                                    description = task.description,
                                    notes = task.notes,
                                    priority = task.priority,
                                    dueDate = task.dueDate,
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
                .addOnFailureListener {
                    viewModelScope.launch {
                        _getTask.value = taskDao.getAllTasks()
                    }
                }
        }
    }


    private val _activity = MutableStateFlow<List<ActivityDtoItem>?>(null)
    val activity: StateFlow<List<ActivityDtoItem>?> = _activity

    fun loadActivityByType(type: String) {
        viewModelScope.launch {
            try {
                val result = repository.getRandomActivity(type)
                _activity.value = result
                Log.d("BoredViewModel", "$result")
            } catch (e: Exception) {
                e("BoredViewModel", "Error: ${e.message}")
            }
        }
    }

    fun addTaskToFirestore(
        title: String,
        description: String,
        notes: String,
        priority: String,
        dueDate: String,
        imageUri: String?,
        isCompleted: Boolean,
        timestamp: Long,
        onSuccess: (Boolean, String) -> Unit
    ) {
        auth.currentUser?.uid?.let { userId ->

            try {
                val taskRef = db.collection(userId).document()
                val taskId = taskRef.id

                val newTask = PostTask(
                    id = taskId,
                    title = title,
                    description = description,
                    notes = notes,
                    priority = priority,
                    dueDate = dueDate,
                    imageUri = imageUri,
                    completed = isCompleted,
                    timestamp = timestamp,
                    userId = userId
                )

                taskRef.set(newTask)
                    .addOnSuccessListener { onSuccess(true, "Task added successfully") }
                    .addOnFailureListener { e ->
                        onSuccess(false, e.message ?: "Unknown error")
                    }

            } catch (e: Exception) {
                onSuccess(false, e.message ?: "Unknown exception")
            }
        }
    }

    fun deleteTaskFromFirestore(
        taskId: String,
        onResult: (Boolean, String) -> Unit
    ) {
        auth.currentUser?.uid?.let { userId ->
            try {
                val taskRef = db.collection(userId).document(taskId)

                taskRef.delete()
                    .addOnSuccessListener {
                        onResult(true, "Task deleted successfully")
                        Log.d("Firestore", "Task deleted successfully with ID: $taskId")
                    }
                    .addOnFailureListener { e ->
                        onResult(false, "Failed to delete task: ${e.message}")
                        Log.e("Firestore", "Error deleting task", e)
                    }

            } catch (e: Exception) {
                onResult(false, "Exception: ${e.message}")
                Log.e("Firestore", "Exception while deleting task: ${e.message}")
            }
        }

    }


    fun toggleTaskCompletion(
        taskId: String,
        currentStatus: Boolean,
        onResult: (Boolean, String) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return onResult(false, "User not logged in")

        val newStatus = !currentStatus

        db.collection(userId).document(taskId)
            .update("completed", newStatus)
            .addOnSuccessListener {
                onResult(true, "Task marked as ${if (newStatus) "completed" else "incomplete"}")
            }
            .addOnFailureListener { e ->
                onResult(false, "Failed to update task: ${e.message}")

            }
    }

    fun updateTaskInFirestore(
        taskId: String,
        title: String,
        description: String,
        notes: String,
        priority: String,
        dueDate: String,
        imageUri: String?,
        onResult: (Boolean, String) -> Unit
    ) {
        auth.currentUser?.uid?.let { userId ->

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
                .addOnSuccessListener { onResult(true, "Task updated") }
                .addOnFailureListener { e ->
                    onResult(false, e.message ?: "Failed")
                }
        }
    }

    fun saveItem(
        onDone: (Boolean, String) -> Unit
    ) {
        val state = _addEditState.value
        val timestamp = System.currentTimeMillis()

        addTaskToFirestore(
            title = state.title,
            description = state.notes,   // You use notes as description
            notes = state.notes,
            priority = state.priority,
            dueDate = state.dueDate,
            imageUri = state.imageUri,
            isCompleted = false,
            timestamp = timestamp
        ) { success, msg ->

            if (!success) {
                onDone(false, msg)
                return@addTaskToFirestore
            }

            // Save locally
            viewModelScope.launch {
                taskDao.upsert(
                    TaskEntity(
                        id = UUID.randomUUID().toString(),
                        title = state.title,
                        description = state.notes,
                        notes = state.notes,
                        priority = state.priority,
                        dueDate = state.dueDate,
                        imageUri = state.imageUri,
                        completed = false,
                        timestamp = timestamp,
                        userId = auth.currentUser?.uid ?: ""
                    )
                )
                _getTask.value = taskDao.getAllTasks()
            }

            onDone(true, "Saved successfully")
        }
    }
}

    data class AddEditUiState(
    val title: String = "",
    val notes: String = "",
    val priority: String = "Normal",
    val dueDate: String = "",
    val imageUri: String? = null
)

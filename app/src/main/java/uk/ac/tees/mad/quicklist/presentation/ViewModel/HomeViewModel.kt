package uk.ac.tees.mad.quicklist.presentation.ViewModel

<<<<<<< HEAD
import android.content.Context
=======
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
<<<<<<< HEAD
import dagger.hilt.android.qualifiers.ApplicationContext
=======
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
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
<<<<<<< HEAD
import uk.ac.tees.mad.quicklist.notification.NotificationScheduler
import java.io.File
=======
import java.io.File
import java.util.UUID
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskDao: TaskDao,
<<<<<<< HEAD
    private val repository: BoredRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

=======
    private val repository: BoredRepository
) : ViewModel() {
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
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

<<<<<<< HEAD
    fun onDueDateChanged(value: Long) {
        _addEditState.value = _addEditState.value.copy(dueDate = value)
    }

    fun onImageCaptured(imagePath: String?) {
        _addEditState.value = _addEditState.value.copy(imageUri = imagePath)
    }

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
=======
    fun onDueDateChanged(value: String) {
        _addEditState.value = _addEditState.value.copy(dueDate = value)
    }

    fun onImageCaptured(imagePath: String?) {  // Now takes file path string
        _addEditState.value = _addEditState.value.copy(imageUri = imagePath)
    }

    val db = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c

    private val _getTask = MutableStateFlow<List<TaskEntity>>(emptyList())
    val getTask: StateFlow<List<TaskEntity>> = _getTask

<<<<<<< HEAD
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
=======
    // For unsigned uploads, remove api_secret to avoid issues (though not causing the error)
    val cloudinaryConfig = Cloudinary(
        mapOf(
            "cloud_name" to "dapd8k4kg",
            "api_key" to "613359997375846",
             "api_secret" to "XgaflLFQ2ml4x2JWRx4pdvMAEFY"
        )
    )

    fun uploadImage(
        filePath: String,  // Now takes file path (absolute path to temp file)
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Verify file exists
                val file = File(filePath)
                if (!file.exists() || !file.canRead()) {
                    val error = "File does not exist or not readable: $filePath"
                    Log.e("HomeViewModel", error)
                    withContext(Dispatchers.Main) { onResult(false, error) }
                    return@launch
                }

                val uploader = cloudinaryConfig.uploader()

                val result = uploader.upload(
                    filePath,  // Pass absolute file path
                    mapOf(
                        "upload_preset" to "quicklist_unsigned",
                        "folder" to "quicklist/tasks"
                    )
                )

                val url = result["secure_url"] as? String
                if (url != null) {
                    Log.d("HomeViewModel", "Upload success: $url")
                    withContext(Dispatchers.Main) { onResult(true, url) }
                } else {
                    val error = "No secure URL returned"
                    Log.e("HomeViewModel", error)
                    withContext(Dispatchers.Main) { onResult(false, error) }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Upload failed", e)
                withContext(Dispatchers.Main) { onResult(false, e.message) }
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
            }
        }
    }

    fun deleteTaskLocally(taskId: String) {
<<<<<<< HEAD
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.deleteTaskById(taskId)
            NotificationScheduler.cancelNotification(context, taskId)
=======
        viewModelScope.launch {
            taskDao.deleteTaskById(taskId)
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
            _getTask.value = taskDao.getAllTasks()
        }
    }

<<<<<<< HEAD
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
=======
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
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
    }

    private val _activity = MutableStateFlow<List<ActivityDtoItem>?>(null)
    val activity: StateFlow<List<ActivityDtoItem>?> = _activity

    fun loadActivityByType(type: String) {
        viewModelScope.launch {
            try {
<<<<<<< HEAD
                _activity.value = repository.getRandomActivity(type)
            } catch (e: Exception) {
                Log.e("BoredViewModel", e.message ?: "")
=======
                val result = repository.getRandomActivity(type)
                _activity.value = result
                Log.d("BoredViewModel", "$result")
            } catch (e: Exception) {
                Log.e("BoredViewModel", "Error: ${e.message}")
            }
        }
    }

    // Updated to return taskId on success
    fun addTaskToFirestore(
        title: String,
        description: String,
        notes: String,
        priority: String,
        dueDate: String,
        imageUri: String?,
        isCompleted: Boolean,
        timestamp: Long,
        onSuccess: (Boolean, String, String?) -> Unit  // Added taskId param
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
                    .addOnSuccessListener {
                        // Firestore callbacks run on Main, but to be safe
                        onSuccess(true, "Task added successfully", taskId)
                    }
                    .addOnFailureListener { e ->
                        onSuccess(false, e.message ?: "Unknown error", null)
                    }
            } catch (e: Exception) {
                onSuccess(false, e.message ?: "Unknown exception", null)
            }
        } ?: onSuccess(false, "User not authenticated", null)
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

    // Updated saveItem: Upload image first (if present), then save to Firestore + local DB using Firestore ID
    // All callbacks now dispatched to Main
    fun saveItem(
        onDone: (Boolean, String) -> Unit
    ) {
        val state = _addEditState.value
        val timestamp = System.currentTimeMillis()

        // Ensure onDone is called on Main
        val safeOnDone: (Boolean, String) -> Unit = { success, msg ->
            viewModelScope.launch(Dispatchers.Main) {
                onDone(success, msg)
            }
        }

        if (state.imageUri != null && state.imageUri!!.isNotEmpty()) {
            // Has image: Upload first, then save
            uploadImage(state.imageUri!!) { uploadSuccess, uploadedUri ->
                if (!uploadSuccess) {
                    safeOnDone(false, uploadedUri ?: "Upload failed")
                    return@uploadImage
                }

                addTaskToFirestore(
                    title = state.title,
                    description = state.notes,
                    notes = state.notes,
                    priority = state.priority,
                    dueDate = state.dueDate,
                    imageUri = uploadedUri,
                    isCompleted = false,
                    timestamp = timestamp
                ) { firestoreSuccess, msg, taskId ->
                    if (!firestoreSuccess) {
                        safeOnDone(false, msg)
                        return@addTaskToFirestore
                    }

                    // Save to local DB using Firestore taskId
                    viewModelScope.launch(Dispatchers.IO) {
                        taskDao.upsert(
                            TaskEntity(
                                id = taskId!!,
                                title = state.title,
                                description = state.notes,
                                notes = state.notes,
                                priority = state.priority,
                                dueDate = state.dueDate,
                                imageUri = uploadedUri,
                                completed = false,
                                timestamp = timestamp,
                                userId = auth.currentUser?.uid ?: ""
                            )
                        )
                        // Refresh on Main
                        launch(Dispatchers.Main) {
                            _getTask.value = taskDao.getAllTasks()
                        }
                    }

                    safeOnDone(true, "Saved successfully")
                }
            }
        } else {
            // No image: Save directly
            addTaskToFirestore(
                title = state.title,
                description = state.notes,
                notes = state.notes,
                priority = state.priority,
                dueDate = state.dueDate,
                imageUri = null,
                isCompleted = false,
                timestamp = timestamp
            ) { firestoreSuccess, msg, taskId ->
                if (!firestoreSuccess) {
                    safeOnDone(false, msg)
                    return@addTaskToFirestore
                }

                // Save to local DB using Firestore taskId
                viewModelScope.launch(Dispatchers.IO) {
                    taskDao.upsert(
                        TaskEntity(
                            id = taskId!!,
                            title = state.title,
                            description = state.notes,
                            notes = state.notes,
                            priority = state.priority,
                            dueDate = state.dueDate,
                            imageUri = null,
                            completed = false,
                            timestamp = timestamp,
                            userId = auth.currentUser?.uid ?: ""
                        )
                    )
                    // Refresh on Main
                    launch(Dispatchers.Main) {
                        _getTask.value = taskDao.getAllTasks()
                    }
                }

                safeOnDone(true, "Saved successfully")
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
            }
        }
    }
}

data class AddEditUiState(
    val title: String = "",
    val notes: String = "",
    val priority: String = "Normal",
<<<<<<< HEAD
    val dueDate: Long = 0L,
    val imageUri: String? = null
)
=======
    val dueDate: String = "",
    val imageUri: String? = null
)
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c

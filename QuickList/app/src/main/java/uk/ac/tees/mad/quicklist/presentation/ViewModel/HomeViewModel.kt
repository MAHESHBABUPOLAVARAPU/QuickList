package uk.ac.tees.mad.quicklist.presentation.ViewModel

import android.R.attr.description
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable.isCompleted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uk.ac.tees.mad.safeher.presentation.ViewModel.GetUserInfo
import java.util.StringTokenizer
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    val db = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _currentUserData = MutableStateFlow(GetUserInfo())
    val currentUserData: StateFlow<GetUserInfo> = _currentUserData


    private val _getTask = MutableStateFlow<List<GetTask>>(emptyList())
    val getTask: StateFlow<List<GetTask>> = _getTask


    fun fetchCurrentUserData() {
        auth.currentUser?.uid?.let { userId ->

            db.collection("user").document(userId).addSnapshotListener { snapshot, e ->

                if (e != null) {

                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.toObject(GetUserInfo::class.java)
                    data?.let {
                        _currentUserData.value = it
                        Log.d("Firestore","$it")
                    }
                }
            }
        }
    }




    fun fetchTasks() {

        auth.currentUser?.uid?.let { userId ->

            db.collection(userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val tasks = snapshot.toObjects(GetTask::class.java)
                    _getTask.value = tasks
                }
                .addOnFailureListener { e ->
                }

        }

    }




    fun addTaskToFirestore(
        title: String,
        description: String,
        isCompleted: Boolean,
        timestamp: Long,
        onSuccess :(Boolean,String) -> Unit
    ) {
        auth.currentUser?.uid?.let { userId ->

            try {
                val taskRef = db.collection(userId).document()

                val taskId = taskRef.id

                val newTask = PostTask(
                    id = taskId,
                    title = title,
                    description = description,
                    completed = isCompleted,
                    timestamp = timestamp,
                    userId = userId
                )
                taskRef.set(newTask)
                    .addOnSuccessListener {
                        onSuccess(true,"Task added successfully")
                        Log.d("Firestore", "Task added successfully with ID: $taskId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error adding task", e)
                    }

            } catch (e: Exception) {
                Log.e("Firestore", "Exception while adding task: ${e.message}")

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
        onResult: (Boolean, String) -> Unit
    ) {
        auth.currentUser?.uid?.let { userId ->

            try {
                val taskRef = db.collection(userId).document(taskId)

                val updates = mapOf(
                    "title" to title,
                    "description" to description
                )

                taskRef.update(updates)
                    .addOnSuccessListener {
                        onResult(true, "Task updated successfully")
                        Log.d("Firestore", "Task $taskId updated successfully")
                    }
                    .addOnFailureListener { e ->
                        onResult(false, "Failed to update task: ${e.message}")
                        Log.e("Firestore", "Error updating task", e)
                    }

            } catch (e: Exception) {
                onResult(false, "Exception while updating: ${e.message}")
                Log.e("Firestore", "Exception while updating task: ${e.message}")
            }
        }
    }







}






data class PostTask(

    val id: String ,
    val title: String ,
    val description: String ,
    val completed: Boolean,
    val timestamp: Long ,
    val userId: String
)


data class GetTask(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val completed: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String = ""
)

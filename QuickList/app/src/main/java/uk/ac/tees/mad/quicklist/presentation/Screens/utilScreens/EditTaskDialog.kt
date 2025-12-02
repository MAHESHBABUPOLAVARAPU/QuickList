package uk.ac.tees.mad.quicklist.presentation.Screens.utilScreens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import uk.ac.tees.mad.quicklist.data.local.TaskEntity
import uk.ac.tees.mad.quicklist.presentation.ViewModel.HomeViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    task: TaskEntity,
    viewModel: HomeViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Load task data into state on dialog open
    LaunchedEffect(Unit) {
        viewModel.onTitleChanged(task.title)
        viewModel.onNotesChanged(task.notes ?: "")
        viewModel.onPriorityChanged(task.priority ?: "Normal")
        viewModel.onDueDateChanged(task.dueDate ?: "")
        // For image, we'll handle separately - don't set local path, keep existing URL
    }

    val state by viewModel.addEditState.collectAsState()
    var tempFile by remember { mutableStateOf<File?>(null) }
    var newImagePath by remember { mutableStateOf<String?>(null) } // For new local image path

    val currentImageUri = task.imageUri // Existing URL

    fun createTempFile(): Pair<File, Uri> {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(context.cacheDir, "IMG_$timestamp.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        return Pair(file, uri)
    }

    // CAMERA LAUNCHER for edit
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempFile != null) {
            newImagePath = tempFile!!.absolutePath
        } else {
            newImagePath = null
            tempFile?.delete()
        }
        tempFile = null
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val (file, uri) = createTempFile()
            tempFile = file
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Image", fontWeight = FontWeight.Bold)

                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .background(Color(0xFFE9F4FF), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!newImagePath.isNullOrEmpty()) {
                                    // Show new captured image
                                    Image(
                                        painter = rememberAsyncImagePainter("file://$newImagePath"),
                                        contentDescription = "New Image",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else if (!currentImageUri.isNullOrEmpty()) {
                                    // Show existing image
                                    Image(
                                        painter = rememberAsyncImagePainter(currentImageUri),
                                        contentDescription = "Current Image",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Text("No Image")
                                }
                            }

                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                modifier = Modifier.padding(top = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF9DE1FF),
                                    contentColor = Color.Black
                                )
                            ) { Text("Capture New Image") }
                        }
                    }
                }

                // Fields Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = state.title,
                                onValueChange = viewModel::onTitleChanged,
                                label = { Text("Title") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            var expanded by remember { mutableStateOf(false) }

                            Box {
                                OutlinedTextField(
                                    value = state.priority,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Priority") },
                                    trailingIcon = {
                                        IconButton(onClick = { expanded = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    listOf("Low", "Normal", "High").forEach { p ->
                                        DropdownMenuItem(
                                            text = { Text(p) },
                                            onClick = {
                                                viewModel.onPriorityChanged(p)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = state.dueDate,
                                onValueChange = viewModel::onDueDateChanged,
                                label = { Text("Due Date (YYYY-MM-DD)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = state.notes,
                                onValueChange = viewModel::onNotesChanged,
                                label = { Text("Notes") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val timestamp = System.currentTimeMillis() // Not used for update, but keep
                    if (newImagePath != null) {
                        // Upload new image first
                        viewModel.uploadImage(newImagePath!!) { uploadSuccess, uploadedUri ->
                            if (uploadSuccess) {
                                viewModel.updateTaskInFirestore(
                                    taskId = task.id,
                                    title = state.title,
                                    description = state.notes,
                                    notes = state.notes,
                                    priority = state.priority,
                                    dueDate = state.dueDate,
                                    imageUri = uploadedUri
                                ) { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        viewModel.fetchTasks()
                                        onDismiss()
                                    }
                                }
                            } else {
                                Toast.makeText(context, uploadedUri ?: "Upload failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // No new image, update with existing
                        viewModel.updateTaskInFirestore(
                            taskId = task.id,
                            title = state.title,
                            description = state.notes,
                            notes = state.notes,
                            priority = state.priority,
                            dueDate = state.dueDate,
                            imageUri = currentImageUri
                        ) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) {
                                viewModel.fetchTasks()
                                onDismiss()
                            }
                        }
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
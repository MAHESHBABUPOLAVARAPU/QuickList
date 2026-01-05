package uk.ac.tees.mad.quicklist.presentation.Screens.utilScreens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
<<<<<<< HEAD
import androidx.compose.foundation.clickable
=======
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
<<<<<<< HEAD
import androidx.compose.material.icons.filled.*
=======
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
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

<<<<<<< HEAD
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

=======
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
    // Load task data into state on dialog open
    LaunchedEffect(Unit) {
        viewModel.onTitleChanged(task.title)
        viewModel.onNotesChanged(task.notes ?: "")
        viewModel.onPriorityChanged(task.priority ?: "Normal")
<<<<<<< HEAD
        viewModel.onDueDateChanged(task.dueDate) // Now accepts Long
=======
        viewModel.onDueDateChanged(task.dueDate ?: "")
        // For image, we'll handle separately - don't set local path, keep existing URL
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
    }

    val state by viewModel.addEditState.collectAsState()
    var tempFile by remember { mutableStateOf<File?>(null) }
<<<<<<< HEAD
    var newImagePath by remember { mutableStateOf<String?>(null) }

    val currentImageUri = task.imageUri
=======
    var newImagePath by remember { mutableStateOf<String?>(null) } // For new local image path

    val currentImageUri = task.imageUri // Existing URL
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c

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

<<<<<<< HEAD
=======
    // CAMERA LAUNCHER for edit
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
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

<<<<<<< HEAD
    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = if (state.dueDate != 0L) state.dueDate else System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDate ->
                            val calendar = Calendar.getInstance()
                            if (state.dueDate != 0L) {
                                calendar.timeInMillis = state.dueDate
                            }
                            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                            val currentMinute = calendar.get(Calendar.MINUTE)

                            val newCalendar = Calendar.getInstance()
                            newCalendar.timeInMillis = selectedDate
                            newCalendar.set(Calendar.HOUR_OF_DAY, currentHour)
                            newCalendar.set(Calendar.MINUTE, currentMinute)
                            newCalendar.set(Calendar.SECOND, 0)
                            newCalendar.set(Calendar.MILLISECOND, 0)

                            viewModel.onDueDateChanged(newCalendar.timeInMillis)
                            showDatePicker = false
                            showTimePicker = true
                        }
                    }
                ) {
                    Text("Next")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        if (state.dueDate != 0L) {
            calendar.timeInMillis = state.dueDate
        }

        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newCalendar = Calendar.getInstance()
                        if (state.dueDate != 0L) {
                            newCalendar.timeInMillis = state.dueDate
                        }
                        newCalendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        newCalendar.set(Calendar.MINUTE, timePickerState.minute)
                        newCalendar.set(Calendar.SECOND, 0)
                        newCalendar.set(Calendar.MILLISECOND, 0)

                        viewModel.onDueDateChanged(newCalendar.timeInMillis)
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Select Time",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Edit Task", fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
=======
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
            ) {
                // Image Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
<<<<<<< HEAD
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Task Image", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(180.dp)
                                    .background(Color(0xFFE9F4FF), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    !newImagePath.isNullOrEmpty() -> {
                                        Image(
                                            painter = rememberAsyncImagePainter("file://$newImagePath"),
                                            contentDescription = "New Image",
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    !currentImageUri.isNullOrEmpty() -> {
                                        Image(
                                            painter = rememberAsyncImagePainter(currentImageUri),
                                            contentDescription = "Current Image",
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    else -> {
                                        Text("No Image", color = Color.Gray)
                                    }
=======
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
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
                                }
                            }

                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
<<<<<<< HEAD
                                modifier = Modifier.padding(top = 12.dp),
=======
                                modifier = Modifier.padding(top = 8.dp),
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF9DE1FF),
                                    contentColor = Color.Black
                                )
<<<<<<< HEAD
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Update Image")
                            }
=======
                            ) { Text("Capture New Image") }
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
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
<<<<<<< HEAD
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
=======
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
                        ) {
                            OutlinedTextField(
                                value = state.title,
                                onValueChange = viewModel::onTitleChanged,
                                label = { Text("Title") },
                                modifier = Modifier.fillMaxWidth(),
<<<<<<< HEAD
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Default.Title, contentDescription = null, tint = Color(0xFF2196F3))
                                }
=======
                                singleLine = true
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
                            )

                            var expanded by remember { mutableStateOf(false) }

                            Box {
                                OutlinedTextField(
                                    value = state.priority,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Priority") },
<<<<<<< HEAD
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Flag,
                                            contentDescription = null,
                                            tint = when (state.priority) {
                                                "High" -> Color(0xFFE53935)
                                                "Normal" -> Color(0xFFFFA726)
                                                "Low" -> Color(0xFF66BB6A)
                                                else -> Color.Gray
                                            }
                                        )
                                    },
=======
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
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
<<<<<<< HEAD
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Flag,
                                                        contentDescription = null,
                                                        tint = when (p) {
                                                            "High" -> Color(0xFFE53935)
                                                            "Normal" -> Color(0xFFFFA726)
                                                            "Low" -> Color(0xFF66BB6A)
                                                            else -> Color.Gray
                                                        }
                                                    )
                                                    Text(p)
                                                }
                                            },
=======
                                            text = { Text(p) },
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
                                            onClick = {
                                                viewModel.onPriorityChanged(p)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }

<<<<<<< HEAD
                            // Date and Time Picker
                            OutlinedTextField(
                                value = if (state.dueDate != 0L) {
                                    SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
                                        .format(Date(state.dueDate))
                                } else {
                                    ""
                                },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Reminder Date & Time") },
                                placeholder = { Text("Tap to select") },
                                trailingIcon = {
                                    Row {
                                        IconButton(onClick = { showDatePicker = true }) {
                                            Icon(
                                                Icons.Default.CalendarMonth,
                                                contentDescription = "Select Date",
                                                tint = Color(0xFF2196F3)
                                            )
                                        }
                                        IconButton(onClick = { showTimePicker = true }) {
                                            Icon(
                                                Icons.Default.AccessTime,
                                                contentDescription = "Select Time",
                                                tint = Color(0xFF4CAF50)
                                            )
                                        }
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = Color(0xFFFF9800)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDatePicker = true },
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                enabled = false
=======
                            OutlinedTextField(
                                value = state.dueDate,
                                onValueChange = viewModel::onDueDateChanged,
                                label = { Text("Due Date (YYYY-MM-DD)") },
                                modifier = Modifier.fillMaxWidth()
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
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
<<<<<<< HEAD
            Button(
                onClick = {
                    if (state.title.isEmpty()) {
                        Toast.makeText(context, "Title is required", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (state.dueDate == 0L) {
                        Toast.makeText(context, "Please select a date and time", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

=======
            TextButton(
                onClick = {
                    val timestamp = System.currentTimeMillis() // Not used for update, but keep
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
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
<<<<<<< HEAD
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save Changes")
=======
                }
            ) {
                Text("Save")
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
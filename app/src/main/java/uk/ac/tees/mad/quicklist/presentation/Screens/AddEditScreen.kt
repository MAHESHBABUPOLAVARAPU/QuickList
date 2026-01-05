package uk.ac.tees.mad.quicklist.presentation.Screens

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import uk.ac.tees.mad.quicklist.presentation.ViewModel.HomeViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val context = LocalContext.current
    val state by viewModel.addEditState.collectAsState()

    var tempFile by remember { mutableStateOf<File?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Permission launcher for notifications (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Notification permission is recommended for reminders", Toast.LENGTH_LONG).show()
        }
    }

    // Request notification permission on Android 13+
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

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

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempFile != null) {
            viewModel.onImageCaptured(tempFile!!.absolutePath)
            tempFile = null
        } else {
            viewModel.onImageCaptured(null)
            tempFile?.delete()
            tempFile = null
        }
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
                            // Preserve existing time or set to current time
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
                        "Select Reminder Time",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF6F9FF),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add / Edit Item",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.saveItem { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

                        if (success) {
                            android.os.Handler(Looper.getMainLooper()).postDelayed({
                                navController.popBackStack()
                            }, 500)
                        }
                    }
                },
                containerColor = Color(0xFF9DE1FF),
                contentColor = Color.Black,
                text = { Text("Save") },
                icon = { Icon(Icons.Default.Check, contentDescription = null) }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Image", fontWeight = FontWeight.Bold)

                        val finalImg = state.imageUri

                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .background(Color(0xFFE9F4FF), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!finalImg.isNullOrEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter("file://$finalImg"),
                                    contentDescription = "Captured Image",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text("No Image Selected", color = Color.Gray)
                            }
                        }

                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                            modifier = Modifier.padding(top = 14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9DE1FF),
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Capture Image")
                        }
                    }
                }
            }

            item {
                ElevatedCard(
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
                            placeholder = { Text("Enter task title") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Title, contentDescription = null, tint = Color(0xFF2196F3))
                            }
                        )

                        var expanded by remember { mutableStateOf(false) }

                        Box {
                            OutlinedTextField(
                                value = state.priority,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Priority") },
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
                                        onClick = {
                                            viewModel.onPriorityChanged(p)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Date and Time Picker Field
                        OutlinedTextField(
                            value = if (state.dueDate != 0L) {
                                SimpleDateFormat("EEE, MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
                                    .format(Date(state.dueDate))
                            } else {
                                ""
                            },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Reminder Date & Time") },
                            placeholder = { Text("Tap to select date and time") },
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
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            enabled = false
                        )

                        OutlinedTextField(
                            value = state.notes,
                            onValueChange = viewModel::onNotesChanged,
                            label = { Text("Notes") },
                            placeholder = { Text("Add any additional notes...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "QuickList – Add / Edit Item")
@Composable
fun QuickListAddEditExactPreview() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF6F9FF),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add / Edit Item",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {},
                containerColor = Color(0xFF9DE1FF),
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Save")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Image Section
            item {
                ElevatedCard(
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
                                .size(240.dp)
                                .background(Color(0xFFE9F4FF), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Image Selected", color = Color.Gray)
                        }

                        Button(
                            onClick = {},
                            modifier = Modifier.padding(top = 14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9DE1FF),
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Capture Image")
                        }
                    }
                }
            }

            // Form Section
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = "Buy groceries",
                            onValueChange = {},
                            label = { Text("Title") },
                            placeholder = { Text("Enter task title") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Title, null, tint = Color(0xFF2196F3))
                            }
                        )

                        // Priority Dropdown
                        OutlinedTextField(
                            value = "Normal",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Priority") },
                            leadingIcon = {
                                Icon(Icons.Default.Flag, null, tint = Color(0xFFFFA726))
                            },
                            trailingIcon = {
                                IconButton(onClick = {}) {
                                    Icon(Icons.Default.ArrowDropDown, null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Date & Time Picker
                        OutlinedTextField(
                            value = "Wed, Oct 15, 2025 at 03:00 PM",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Reminder Date & Time") },
                            placeholder = { Text("Tap to select date and time") },
                            leadingIcon = {
                                Icon(Icons.Default.Notifications, null, tint = Color(0xFFFF9800))
                            },
                            trailingIcon = {
                                Row {
                                    IconButton(onClick = {}) {
                                        Icon(Icons.Default.CalendarMonth, null, tint = Color(0xFF2196F3))
                                    }
                                    IconButton(onClick = {}) {
                                        Icon(Icons.Default.AccessTime, null, tint = Color(0xFF4CAF50))
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false
                        )

                        OutlinedTextField(
                            value = "Milk, eggs, bread, fruits",
                            onValueChange = {},
                            label = { Text("Notes") },
                            placeholder = { Text("Add any additional notes...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }
            }
        }
    }
}
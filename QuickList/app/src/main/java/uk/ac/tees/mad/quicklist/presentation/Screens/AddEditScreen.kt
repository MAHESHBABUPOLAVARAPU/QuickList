package uk.ac.tees.mad.quicklist.presentation.Screens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen() {

    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Normal") }
    var dueDate by remember { mutableStateOf("") }

    val imageUri = remember { mutableStateOf<Uri?>(null) }

    // ---------------------------------------------------
    // CAMERA + FILE PROVIDER LOGIC
    // ---------------------------------------------------
    fun createImageUri(): Uri? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(context.cacheDir, "IMG_$timestamp.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) imageUri.value = null
        // viewModel.onImageCaptured(imageUri.value)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createImageUri()
            imageUri.value = uri
            cameraLauncher.launch(uri!!)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------------------------------------------------
    // UI LAYOUT
    // ---------------------------------------------------
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
                    // viewModel.saveItem(...)
                },
                containerColor = Color(0xFF9DE1FF),
                contentColor = Color.Black,
                text = { Text("Save") },
                icon = { Icon(Icons.Default.Check, contentDescription = null) }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ---------------------------------------------------
            // IMAGE CARD
            // ---------------------------------------------------
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {

                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "Image",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .background(Color(0xFFE9F4FF), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri.value != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri.value),
                                contentDescription = "Captured Image",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("No Image Selected")
                        }
                    }

                    Button(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.padding(top = 14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9DE1FF),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Capture Image")
                    }
                }
            }

            // ---------------------------------------------------
            // FORM CARD
            // ---------------------------------------------------
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {

                Column(
                    modifier = Modifier
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            // viewModel.onTitleChanged(it)
                        },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Priority
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedTextField(
                            value = priority,
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
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf("Low", "Normal", "High").forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p) },
                                    onClick = {
                                        priority = p
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Due Date
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = {
                            dueDate = it
                            // viewModel.onDueDateChanged(it)
                        },
                        label = { Text("Due Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = {
                            notes = it
                            // viewModel.onNotesChanged(it)
                        },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }
        }
    }
}

package uk.ac.tees.mad.quicklist.presentation.Screens

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
import uk.ac.tees.mad.quicklist.presentation.ViewModel.HomeViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.addEditState.collectAsState()

    var tempUri by remember { mutableStateOf<Uri?>(null) }

    fun createImageUri(): Uri {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(context.cacheDir, "IMG_$timestamp.jpg")

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    // CAMERA LAUNCHER
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempUri != null) {
            viewModel.onImageCaptured(tempUri.toString())
        } else {
            viewModel.onImageCaptured(null)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createImageUri()
            tempUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF6F9FF),
        topBar = {
            TopAppBar(
                title = {
                    Text("Add / Edit Item",
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
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
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
                                    painter = rememberAsyncImagePainter(finalImg),
                                    contentDescription = "Captured Image",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text("No Image Selected")
                            }
                        }

                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                            modifier = Modifier.padding(top = 14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9DE1FF),
                                contentColor = Color.Black
                            )
                        ) { Text("Capture Image") }
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
    }
}

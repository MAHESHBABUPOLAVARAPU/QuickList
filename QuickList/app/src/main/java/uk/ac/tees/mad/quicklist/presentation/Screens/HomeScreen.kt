package uk.ac.tees.mad.quicklist.presentation.Screens

import BottomNavigation
import android.Manifest
import android.R.attr.description
import android.R.id.message
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.NonCancellable.isCompleted
import uk.ac.tees.mad.quicklist.data.local.TaskEntity
import uk.ac.tees.mad.quicklist.presentation.Screens.utilScreens.EditTaskDialog
import uk.ac.tees.mad.quicklist.presentation.ViewModel.GetTask
import uk.ac.tees.mad.quicklist.presentation.ViewModel.HomeViewModel
import uk.ac.tees.mad.safeher.presentation.Screens.utilScreens.AddTaskDialog
import uk.ac.tees.mad.safeher.presentation.ViewModel.AuthViewModel
import uk.ac.tees.mad.safeher.presentation.navigation.Routes
import kotlin.coroutines.coroutineContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController,
) {

    LaunchedEffect(Unit) {

        homeViewModel.fetchTasks()
    }
    val tasks = homeViewModel.getTask.collectAsState().value

    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }



    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            BottomNavigation(
                navController = navController,
                modifier = Modifier
            )

        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "QuickList",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            )

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.AddEditScreen)

                }, modifier = Modifier.padding(end = 20.dp, bottom = 20.dp),
                containerColor = Color(0xFF9DE1FF),
                contentColor = MaterialTheme.colorScheme.background

            ) {
                Icon(


                    imageVector = if (showDialog) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (showDialog) "Close" else "Add",
                    tint = Color.Black
                )

            }
        },
    ) { paddingValues ->


        if (showDialog) {

            AddTaskDialog(
                context = LocalContext.current,
                onDismiss = { showDialog = false },
                onSave = { title, description ->

                    homeViewModel.addTaskToFirestore(
                        title = title,
                        description = description,
                        isCompleted = false,
                        timestamp = System.currentTimeMillis(),
                        onSuccess = { b, m ->
                            if (b) {
                                homeViewModel.fetchTasks()
                                Toast.makeText(context, m, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, m, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(1) {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                items(tasks) {

                    TaskCard(
                        homeViewModel = homeViewModel,
                        task = it,
                        onDeleteClick = {
                            homeViewModel.deleteTaskFromFirestore(
                                taskId = it.id,
                                onResult = { b, m ->
                                    if (b) {
                                        homeViewModel.fetchTasks()
                                        homeViewModel.deleteTaskLocally(it.id)
                                        Toast.makeText(context, m, Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, m, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        },
                        onToggleComplete = {
                            homeViewModel.toggleTaskCompletion(
                                taskId = it.id,
                                currentStatus = it.completed,
                                onResult = { b, m ->
                                    if (b) {
                                        homeViewModel.fetchTasks()
                                        Toast.makeText(context, m, Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, m, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }

                    )
                }
            }
        }
    }

}


@Composable
fun TaskCard(
    task: TaskEntity,
    onDeleteClick: (TaskEntity) -> Unit,
    onToggleComplete: (TaskEntity) -> Unit,
    homeViewModel: HomeViewModel,
) {
    var editTaskDialog by remember { mutableStateOf(false) }
    if (editTaskDialog) {
        val context = LocalContext.current

        EditTaskDialog(
            context = context,
            currentTitle = task.title,
            currentDescription = task.description,
            onDismiss = {
                editTaskDialog = false
            },
            onUpdate = { title, des ->
                homeViewModel.updateTaskInFirestore(
                    taskId = task.id,
                    title = title,
                    description = des,
                    onResult = { b, m ->
                        if (b) {
                            homeViewModel.fetchTasks()
                            Toast.makeText(context, m, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, m, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        )

    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF9DE1FF)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { onToggleComplete(task) }) {

                Icon(
                    imageVector = if (task.completed) Icons.Default.CheckCircle
                    else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Complete",
                    tint = Color.Black
                )
            }


            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = task.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = task.description,
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.8f)
                )
            }


            Row {
                IconButton(onClick = { editTaskDialog = !editTaskDialog }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Task",
                        tint = Color.Black
                    )
                }
                IconButton(onClick = { onDeleteClick(task) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Task",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}
package uk.ac.tees.mad.quicklist.presentation.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uk.ac.tees.mad.quicklist.presentation.ViewModel.GetTask
import uk.ac.tees.mad.quicklist.presentation.ViewModel.HomeViewModel
import uk.ac.tees.mad.safeher.presentation.Screens.utilScreens.AddTaskDialog
import uk.ac.tees.mad.safeher.presentation.ViewModel.AuthViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController,
) {



    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {

       homeViewModel.fetchCurrentUserData()



    }
    val currentUser = homeViewModel.currentUserData.collectAsState().value

    LaunchedEffect(Unit) {


        homeViewModel.fetchTasks("RVZ1PCbae1UaAprMTZnCBGrK2kr1")


    }
    val tasks = homeViewModel.getTask.collectAsState().value
    val PrimaryBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF9DE1FF), Color(0xFFA6ECFF))
    )


    Scaffold(
        modifier = Modifier
            .fillMaxSize(), bottomBar = {

        }, floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = !showDialog

                }, modifier = Modifier.padding(end = 20.dp, bottom = 20.dp),
                containerColor = Color(0xFF9DE1FF),
                contentColor = MaterialTheme.colorScheme.background

            ) {
                Icon(


                    imageVector = if (showDialog) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (showDialog) "Close" else "Add"
                )

            }
        }) { paddingValues ->

        if (showDialog) {

            AddTaskDialog(
                context = LocalContext.current,
                onDismiss = { showDialog = false },
                onSave = { title, description ->

                    homeViewModel.addTaskToFirestore(
                        title = title,
                        description = description ,
                        isCompleted = false,
                        timestamp = System.currentTimeMillis(),
                        userId = currentUser.uid,
                        onSuccess = { t, m ->
                            if (t){
                                homeViewModel.fetchTasks(currentUser.uid)
                            }
                        }
                    )

                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = PrimaryBrush)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {

            LazyColumn(modifier = Modifier
                .fillMaxSize()) {
                items(1){
                    Spacer(modifier = Modifier.height(20.dp))
                }
                items(tasks) {

                    TaskCard(
                        task = it,
                        onEditClick = {},
                        onDeleteClick = {},
                        onToggleComplete = {

                        }
                    )
                }
            }
        }
    }

}


@Composable
fun TaskCard(
    task: GetTask,
    onEditClick: (GetTask) -> Unit,
    onDeleteClick: (GetTask) -> Unit,
    onToggleComplete: (GetTask) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onToggleComplete(task) },
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
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle
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
                IconButton(onClick = { onEditClick(task) }) {
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
package uk.ac.tees.mad.quicklist.presentation.Screens

import BottomNavigation
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
<<<<<<< HEAD
import androidx.compose.ui.tooling.preview.Preview
=======
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import uk.ac.tees.mad.quicklist.data.remote.api.activityDto.ActivityDtoItem
<<<<<<< HEAD
import uk.ac.tees.mad.quicklist.presentation.ViewModel.AuthViewModel
import uk.ac.tees.mad.quicklist.presentation.ViewModel.HomeViewModel
=======
import uk.ac.tees.mad.quicklist.presentation.ViewModel.HomeViewModel
import uk.ac.tees.mad.safeher.presentation.ViewModel.AuthViewModel
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    homeViewModel: HomeViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController,
) {
    LaunchedEffect(Unit) {
        homeViewModel.loadActivityByType("education")
    }

    val activityList by homeViewModel.activity.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Suggested Activities",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF9DE1FF)),
                modifier = Modifier.shadow(8.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            )
        },
        bottomBar = { BottomNavigation(navController = navController) }
    ) { innerPadding ->

<<<<<<< HEAD
=======
        // THIS IS THE KEY FIX: No Box + Center alignment around LazyColumn
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
        when {
            activityList == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF9DE1FF))
                }
            }

            activityList!!.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF9DE1FF), modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No activities found", color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = innerPadding.calculateTopPadding() + 16.dp,
                        bottom = innerPadding.calculateBottomPadding() + 80.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(activityList!!) { activity ->
                        ActivityCard(activity = activity, onLinkClick = { uriHandler.openUri(it) })
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityCard(activity: ActivityDtoItem, onLinkClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = activity.activity,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

<<<<<<< HEAD
=======
            // Type tag
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c
            Surface(color = Color(0xFF9DE1FF).copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tag, contentDescription = null, tint = Color(0xFF9DE1FF), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(activity.type.uppercase(), fontWeight = FontWeight.SemiBold, color = Color(0xFF9DE1FF), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DetailRow(icon = Icons.Default.LocalOffer, label = "Price", value = "$${activity.price}")
                DetailRow(icon = Icons.Default.Groups, label = "Participants", value = activity.participants.toString())
                DetailRow(icon = Icons.Default.AccessTime, label = "Duration", value = activity.duration)
                DetailRow(icon = Icons.Default.ChildCare, label = "Kid Friendly", value = if (activity.kidFriendly) "Yes" else "No")
                DetailRow(icon = Icons.Default.Info, label = "Accessibility", value = activity.accessibility)

                if (activity.link.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLinkClick(activity.link) }
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFF9DE1FF), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Learn More →", color = Color(0xFF9DE1FF), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF6F9FF))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF9DE1FF), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
<<<<<<< HEAD
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "QuickList – Suggested Activities")
@Composable
fun QuickListActivityExactPreview() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Suggested Activities",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF9DE1FF)),
                modifier = Modifier.shadow(8.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(3) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Learn a new programming language",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Surface(color = Color(0xFF9DE1FF).copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Tag, contentDescription = null, tint = Color(0xFF9DE1FF), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("EDUCATION", fontWeight = FontWeight.SemiBold, color = Color(0xFF9DE1FF), fontSize = 12.sp)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            DetailRow(icon = Icons.Default.LocalOffer, label = "Price", value = "$0.00")
                            DetailRow(icon = Icons.Default.Groups, label = "Participants", value = "1")
                            DetailRow(icon = Icons.Default.AccessTime, label = "Duration", value = "Variable")
                            DetailRow(icon = Icons.Default.ChildCare, label = "Kid Friendly", value = "Yes")
                            DetailRow(icon = Icons.Default.Info, label = "Accessibility", value = "1.0")

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFF9DE1FF), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Learn More →", color = Color(0xFF9DE1FF), fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}
=======
}
>>>>>>> e43dfc60c754b6235a780645bfa02ea7e0599c2c

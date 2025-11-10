package uk.ac.tees.mad.quicklist.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import uk.ac.tees.mad.quicklist.R

@Composable
fun SplashScreen(

) {
    LaunchedEffect(key1 = true) {
        delay(2000) // Splash delay (2s)

//        val currentUser = firebaseAuth.currentUser
//        if (currentUser != null) {
//            navController.navigate("home") {
//                popUpTo("splash") { inclusive = true }
//            }
//        } else {
//            navController.navigate("auth") {
//                popUpTo("splash") { inclusive = true }
//            }
//        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.quicklist),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp).clip(RoundedCornerShape(24.dp))
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Fast. Simple. Organized.",
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }
    }
}

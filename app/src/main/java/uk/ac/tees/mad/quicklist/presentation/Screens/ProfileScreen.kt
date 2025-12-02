package uk.ac.tees.mad.quicklist.presentation.Screens

import BottomNavigation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uk.ac.tees.mad.quicklist.presentation.ViewModel.HomeViewModel
import uk.ac.tees.mad.safeher.presentation.ViewModel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(homeViewModel: HomeViewModel,
                  authViewModel: AuthViewModel,
                  navController: NavHostController,) {


   Scaffold (
       topBar = {
           TopAppBar(
               title = {
                   Text(
                       text = "Profile",
                       color = Color.Black,
                       fontWeight = FontWeight.Bold,
                       fontSize = 22.sp
                   )
               },
               colors = TopAppBarDefaults.topAppBarColors(
                   containerColor = Color(0xFF9DE1FF)
               ),
               modifier = Modifier
                   .fillMaxWidth()
                   .shadow(4.dp, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
           )

   },bottomBar = {
       BottomNavigation(
           navController = navController,
           modifier = Modifier
       )
   }){
       paddingValues ->
       Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(paddingValues)){

       }
   }

}
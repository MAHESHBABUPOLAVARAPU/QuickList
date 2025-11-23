package uk.ac.tees.mad.quicklist.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import uk.ac.tees.mad.quicklist.presentation.Screens.ActivityScreen
import uk.ac.tees.mad.quicklist.presentation.Screens.AuthScreen
import uk.ac.tees.mad.quicklist.presentation.Screens.HomeScreen
import uk.ac.tees.mad.quicklist.presentation.Screens.LoginScreen
import uk.ac.tees.mad.quicklist.presentation.Screens.SingInScreen

import uk.ac.tees.mad.safeher.presentation.ViewModel.AuthViewModel
import uk.ac.tees.mad.quicklist.presentation.ViewModel.HomeViewModel
import uk.ac.tees.mad.safeher.presentation.navigation.Routes


@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel, authViewModel: AuthViewModel,
) {

    val navController = rememberNavController()

    val auth = FirebaseAuth.getInstance()


    var currentUser by remember { mutableStateOf(auth.currentUser) }


    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener {
            currentUser = it.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    val startDestination = if (currentUser == null) {
        Routes.AuthScreen
    } else {
        Routes.HomeScreen
    }

    NavHost(navController, startDestination = startDestination) {

        composable<Routes.AuthScreen> {


            AuthScreen(
                homeViewModel = homeViewModel,
                authViewModel = authViewModel,
                navController = navController
            )

        }

        composable<Routes.SingInScreen> {


            SingInScreen(
                homeViewModel = homeViewModel,
                authViewModel = authViewModel,
                navController = navController
            )

        }
        composable<Routes.LoginScreen> {


           LoginScreen(

                homeViewModel = homeViewModel,
                authViewModel = authViewModel,
                navController = navController
            )

        }
        composable<Routes.HomeScreen> {


         HomeScreen(
                homeViewModel = homeViewModel,
                authViewModel = authViewModel,
                navController = navController
            )

        }

        composable<Routes.ActivityScreen> {


            ActivityScreen(
                homeViewModel = homeViewModel,
                authViewModel = authViewModel,
                navController = navController
            )

        }

        composable<Routes.ProfileScreen> {


            ActivityScreen(
                homeViewModel = homeViewModel,
                authViewModel = authViewModel,
                navController = navController
            )

        }





    }


}






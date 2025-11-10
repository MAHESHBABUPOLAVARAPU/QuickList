package uk.ac.tees.mad.quicklist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.quicklist.screens.AuthScreen
import uk.ac.tees.mad.quicklist.screens.SplashScreen
import uk.ac.tees.mad.quicklist.ui.theme.QuickListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickListTheme {
                NavigationGraph()
            }
        }
    }
}


sealed class NavigateItems(val routes : String) {
    object Splash : NavigateItems("splash")
    object Auth : NavigateItems ( "auth")
}


@Composable
fun NavigationGraph(){
    val navController = rememberNavController()
    NavHost(navController, NavigateItems.Splash.routes){
        composable(NavigateItems.Splash.routes){
            SplashScreen()
        }
        composable(NavigateItems.Auth.routes){
            AuthScreen()
        }
    }
}
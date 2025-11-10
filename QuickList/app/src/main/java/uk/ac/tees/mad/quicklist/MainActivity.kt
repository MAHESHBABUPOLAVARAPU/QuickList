package uk.ac.tees.mad.quicklist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import uk.ac.tees.mad.quicklist.screens.AuthScreen
import uk.ac.tees.mad.quicklist.screens.SplashScreen
import uk.ac.tees.mad.quicklist.ui.theme.QuickListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickListTheme {
                AuthScreen()
            }
        }
    }
}

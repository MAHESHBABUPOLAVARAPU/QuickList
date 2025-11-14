package uk.ac.tees.mad.quicklist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import uk.ac.tees.mad.quicklist.presentation.ViewModel.HomeViewModel
import uk.ac.tees.mad.quicklist.presentation.navigation.Navigation
import uk.ac.tees.mad.quicklist.ui.theme.QuickListTheme
import uk.ac.tees.mad.safeher.presentation.ViewModel.AuthViewModel
import kotlin.getValue


class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels<HomeViewModel>()
    private val authViewModel: AuthViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            QuickListTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigation(
                        modifier = Modifier.padding(innerPadding),
                        homeViewModel = homeViewModel,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}


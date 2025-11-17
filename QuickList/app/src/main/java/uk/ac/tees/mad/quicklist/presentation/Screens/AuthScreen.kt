package uk.ac.tees.mad.quicklist.presentation.Screens

import android.R.attr.textColor
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import uk.ac.tees.mad.quicklist.R
import uk.ac.tees.mad.quicklist.presentation.ViewModel.HomeViewModel
import uk.ac.tees.mad.safeher.presentation.ViewModel.AuthViewModel
import uk.ac.tees.mad.safeher.presentation.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(homeViewModel: HomeViewModel,authViewModel: AuthViewModel,navController: NavHostController) {
    var selectedTab by rememberSaveable { mutableStateOf("Login") }
    val textColor = Color(0xFF000000)
    val bgColor = Color(0xFF7AE1FF)



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF9DE1FF), Color(0xFFA6ECFF))
                )
            )

    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            topBar = {
                TopAppBar(
                    title = { Text("QuickList", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = textColor

                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.seven_icon),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp).clip(RoundedCornerShape(24.dp))
                )

                Spacer(modifier = Modifier.height(24.dp))

                TabRow(
                    selectedTabIndex = if (selectedTab == "Login") 0 else 1,
                    containerColor = Color.Transparent,
                ) {
                    Tab(
                        selected = selectedTab == "Login",
                        onClick = { selectedTab = "Login" },
                        text = { Text("Login", color = textColor) }
                    )

                    Tab(
                        selected = selectedTab == "Register",
                        onClick = { selectedTab = "Register" },
                        text = { Text("Register",color = textColor) }
                    )



                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                ) {
                    Crossfade(targetState = selectedTab) { screen ->
                        when (screen) {


                            "Login" -> LoginForm(authViewModel = authViewModel,
                                navController = navController)
                            "Register" -> RegisterForm(
                                authViewModel = authViewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun RegisterForm(authViewModel: AuthViewModel,navController: NavHostController) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var triggeer by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(triggeer) {
        delay(3000)
        if (passwordVisible){
            passwordVisible = !passwordVisible
        }

    }
    val context = LocalContext.current
    val cornerShape = RoundedCornerShape(14.dp)
    val textColor = Color(0xFF000000)
    val bgColor = Color(0xFF91E3FF)
    Column(modifier = Modifier.padding(24.dp)) {
        Text("Create Your Account", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { input ->
                name = input.split(" ").joinToString(" ") { word ->
                    if (word.isNotEmpty()) word.replaceFirstChar { it.uppercase() }
                    else word
                }
            },
            placeholder = { Text("Name", color = textColor.copy(alpha = 0.6f)) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth(),
            shape = cornerShape,
            maxLines = 1,
            colors = TextFieldDefaults.colors(
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = textColor,
                disabledTextColor = textColor,
                focusedContainerColor = bgColor,
                unfocusedContainerColor = bgColor,
                disabledContainerColor = bgColor,
                focusedIndicatorColor = textColor,
                unfocusedIndicatorColor = textColor,
                disabledIndicatorColor = textColor,
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor,
                disabledLabelColor = textColor
            )

        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email", color = textColor.copy(alpha = 0.6f)) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth(),
            shape = cornerShape, maxLines = 1,
            colors = TextFieldDefaults.colors(
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = textColor,
                disabledTextColor = textColor,
                focusedContainerColor = bgColor,
                unfocusedContainerColor = bgColor,
                disabledContainerColor = bgColor,
                focusedIndicatorColor = textColor,
                unfocusedIndicatorColor = textColor,
                disabledIndicatorColor = textColor,
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor,
                disabledLabelColor = textColor
            )
        )



        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = password,

            onValueChange = { password = it },
            placeholder = { Text("Password", color = textColor.copy(alpha = 0.6f)) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),

            modifier = Modifier.fillMaxWidth(),
            shape = cornerShape,
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                    triggeer = !triggeer
                }) {
                    Icon(
                        painter = painterResource(
                            if (passwordVisible) R.drawable.baseline_visibility_24
                            else R.drawable.outline_visibility_off_24
                        ),
                        contentDescription = null, tint = textColor
                    )
                }
            }, maxLines = 1,
            colors = TextFieldDefaults.colors(
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = textColor,
                disabledTextColor = textColor,
                focusedContainerColor = bgColor,
                unfocusedContainerColor = bgColor,
                disabledContainerColor = bgColor,
                focusedIndicatorColor = textColor,
                unfocusedIndicatorColor = textColor,
                disabledIndicatorColor = textColor,
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor,
                disabledLabelColor = textColor
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = {

                authViewModel.signUp(
                    email = email,
                    password = password,
                    name = name,
                    onResult = { message, success ->
                        if (success) {

                            navController.navigate(Routes.HomeScreen)
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Text("Register", color = Color.Black)
        }
    }
}
@Composable
fun LoginForm(authViewModel: AuthViewModel,navController: NavHostController) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    var triggeer by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(triggeer) {
        delay(3000)
        if (passwordVisible){
            passwordVisible = !passwordVisible
        }

    }
    val cornerShape = RoundedCornerShape(14.dp)
    val textColor = Color(0xFF000000)
    val bgColor = Color(0xFF91E3FF)
    Column(modifier = Modifier.padding(24.dp)) {
        Text("Welcome Back!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email", color = textColor.copy(alpha = 0.6f)) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth(),
            shape = cornerShape, maxLines = 1,
            colors = TextFieldDefaults.colors(
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = textColor,
                disabledTextColor = textColor,
                focusedContainerColor = bgColor,
                unfocusedContainerColor = bgColor,
                disabledContainerColor = bgColor,
                focusedIndicatorColor = textColor,
                unfocusedIndicatorColor = textColor,
                disabledIndicatorColor = textColor,
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor,
                disabledLabelColor = textColor
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = password,

            onValueChange = { password = it },
            placeholder = { Text("Password", color = textColor.copy(alpha = 0.6f)) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),

            modifier = Modifier.fillMaxWidth(),
            shape = cornerShape,
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                    triggeer = !triggeer
                }) {
                    Icon(
                        painter = painterResource(
                            if (passwordVisible) R.drawable.baseline_visibility_24
                            else R.drawable.outline_visibility_off_24
                        ),
                        contentDescription = null, tint = textColor
                    )
                }
            }, maxLines = 1,
            colors = TextFieldDefaults.colors(
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = textColor,
                disabledTextColor = textColor,
                focusedContainerColor = bgColor,
                unfocusedContainerColor = bgColor,
                disabledContainerColor = bgColor,
                focusedIndicatorColor = textColor,
                unfocusedIndicatorColor = textColor,
                disabledIndicatorColor = textColor,
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor,
                disabledLabelColor = textColor
            )
        )



        Spacer(modifier = Modifier.height(24.dp))
        val context = LocalContext.current
        OutlinedButton(
            onClick = { authViewModel.logIn(
                email = email,
                passkey = password,
                onResult = { message, success ->
                    if (success) {

                        navController.navigate(Routes.HomeScreen)
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            ) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Text("Log in", color = Color.Black)
        }

    }
}


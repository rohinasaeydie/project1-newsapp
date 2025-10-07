package com.example.project1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.example.project1.ui.theme.Project1Theme
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1Theme {
                LoginScreen()
            }
        }
    }
}

//pull request
@Composable
fun LoginScreen() {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Shared Preferences
    val prefs = remember { context.getSharedPreferences("news_prefs", Context.MODE_PRIVATE) }

    // Check conditions: username ≥ 5 chars, no spaces; password ≥ 8 chars, no spaces
    val isLoginEnabled = username.length >= 5 &&
            !username.contains(" ") &&
            password.length >= 8 &&
            !password.contains(" ")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.world),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .padding(16.dp)
        )


        Spacer(Modifier.height(16.dp))

        Text("News App Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(24.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                prefs.edit { putString("username", username) }
                val intent = Intent(context, MainSearchActivity::class.java)
                context.startActivity(intent)
            },
            enabled = isLoginEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Login")
        }

        if (!isLoginEnabled) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Username must be ≥ 5 characters (no spaces) and password ≥ 8 characters (no spaces)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
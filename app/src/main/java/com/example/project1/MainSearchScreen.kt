package com.example.project1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.project1.ui.theme.Project1Theme

class MainSearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1Theme {
                MainSearchScreen()
            }
        }
    }
}

@Composable
fun MainSearchScreen() {
    var searchTerm by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

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

        Text("Main Search", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        TextField(
            value = searchTerm,
            onValueChange = {
                searchTerm = it
                errorMessage = ""
            },
            label = { Text("Enter topic") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (searchTerm.isBlank()) {
                    errorMessage = "Search term cannot be empty"
                } else {
                    val intent = Intent(context, SourcesActivity::class.java)
                    intent.putExtra("searchTerm", searchTerm)
                    context.startActivity(intent)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search News Sources")
        }

        Spacer(Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                val intent = Intent(context, MapActivity::class.java)
                context.startActivity(intent)
            }) {
                Text("Map")
            }

            //Button(onClick = {
            //    val intent = Intent(context, SourcesActivity::class.java)
            //    intent.putExtra("searchTerm", searchTerm)
            //    context.startActivity(intent)
            //}) {
            //    Text("Sources")
            //}

            Button(onClick = {
                val intent = Intent(context, TopHeadlinesActivity::class.java)
                context.startActivity(intent)
            }) {
                Text("Top Headlines")
            }

        }
    }
}

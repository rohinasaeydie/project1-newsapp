package com.example.project1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.project1.ui.theme.Project1Theme

data class FakeSource(val name: String, val description: String)

class SourcesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1Theme {
                SourcesScreen(searchTerm = "Android") // Replace with passed term later
            }
        }
    }
}

@Composable
fun SourcesScreen(searchTerm: String) {
    var selectedCategory by remember { mutableStateOf("All") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val categories = listOf("All", "Health", "Sports", "Technology", "Business")

    val fakeSources = listOf(
        FakeSource("NBC4", "DMV news coverage"),
        FakeSource("BBC News", "Global news coverage"),
        FakeSource("ESPN", "Sports")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sources Screen", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Search for: '$searchTerm'", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Category dropdown (stable version)
        Box {
            Button(onClick = { dropdownExpanded = true }) {
                Text("Category: $selectedCategory")
            }
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Skip button
        Button(onClick = {
            // Logic to skip sources
        }) {
            Text("Skip Sources")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fake list of sources
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(fakeSources) { source ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable {
                            // Handle click on source
                        }
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(source.name, style = MaterialTheme.typography.titleMedium)
                        Text(source.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

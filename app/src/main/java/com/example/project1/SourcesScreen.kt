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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SourcesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val searchTerm = intent.getStringExtra("searchTerm") ?: "general"

        setContent {
            Project1Theme {
                SourcesScreen(searchTerm = searchTerm)
            }
        }
    }
}

@Composable
fun SourcesScreen(searchTerm: String) {
    val context = LocalContext.current
    val apiKey = context.getString(R.string.NewsKey)
    val sourcesManager = remember { SourcesManager() }

    var sources by remember { mutableStateOf<List<NewsSource>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("All") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "All", "Business", "Entertainment", "General",
        "Health", "Science", "Sports", "Technology"
    )

    val listState = rememberLazyListState()

    LaunchedEffect(selectedCategory) {
        isLoading = true
        errorMessage = null
        val result = withContext(Dispatchers.IO) {
            try {
                sourcesManager.retrieveSources(apiKey, selectedCategory)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList<NewsSource>()
            }
        }
        if (result.isNotEmpty()) {
            sources = result
        } else {
            errorMessage = "No sources found for \"$selectedCategory\""
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("News Sources", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Search for: \"$searchTerm\"", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))

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

            Button(onClick = {
                val intent = Intent(context, ResultsActivity::class.java)
                intent.putExtra("searchTerm", searchTerm)
                intent.putExtra("selectedSource", null as String?) // No source selected
                context.startActivity(intent)
            }) {
                Text("Skip Sources")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sources) { source ->
                    NewsSourceCard(source = source, searchTerm = searchTerm)
                }
            }
        }
    }
}

@Composable
fun NewsSourceCard(
    source: NewsSource,
    searchTerm: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, ResultsActivity::class.java)
                intent.putExtra("searchTerm", searchTerm)
                intent.putExtra("selectedSource", source.id)
                context.startActivity(intent)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(source.name, style = MaterialTheme.typography.titleMedium)
            Text(source.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text("Category: ${source.category}", style = MaterialTheme.typography.bodySmall)
            Text("Country: ${source.country}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

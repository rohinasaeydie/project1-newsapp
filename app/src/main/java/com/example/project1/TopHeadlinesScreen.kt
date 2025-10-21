package com.example.project1

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TopHeadlinesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Project1Theme {
                TopHeadlinesScreen()
            }
        }
    }
}

@Composable
fun TopHeadlinesScreen() {
    val context = LocalContext.current
    val apiKey = context.getString(R.string.NewsKey)
    val prefs = context.getSharedPreferences("news_prefs", Context.MODE_PRIVATE)
    val articlesManager = remember { ArticlesManager() }

    val categories = listOf(
        "Business", "Entertainment", "General",
        "Health", "Science", "Sports", "Technology"
    )

    var selectedCategory by remember {
        mutableStateOf(prefs.getString("top_headlines_category", "General") ?: "General")
    }
    var page by remember { mutableStateOf(1) }
    var maxPage by remember { mutableStateOf(1) }
    var headlines by remember { mutableStateOf<List<NewsArticle>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Load headlines when category or page changes
    LaunchedEffect(selectedCategory, page) {
        isLoading = true
        errorMessage = null

        val result = withContext(Dispatchers.IO) {
            try {
                articlesManager.getTopHeadlines(apiKey, selectedCategory.lowercase(), page)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList<NewsArticle>()
            }
        }

        if (result.isNotEmpty()) {
            headlines = result
            maxPage = minOf(5, (articlesManager.totalResults + 19) / 20) // free tier limit
        } else {
            headlines = emptyList()
            errorMessage = "No articles found for $selectedCategory"
            Log.d("TopHeadlines", "No articles returned for category: $selectedCategory")
        }

        isLoading = false
        prefs.edit { putString("top_headlines_category", selectedCategory) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Category: ", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                Button(onClick = { dropdownExpanded = true }) {
                    Text(selectedCategory)
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
                                page = 1
                                headlines = emptyList()
                                maxPage = 1
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            errorMessage != null -> Text(errorMessage!!, color = MaterialTheme.colorScheme.error)

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(headlines) { article ->
                        ArticleCard(article) // ensure your ArticleCard shows image if imageUrl is present
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { if (page > 1) page-- }, enabled = page > 1) { Text("Previous") }
                    Text("Page $page of $maxPage", style = MaterialTheme.typography.bodyLarge)
                    Button(onClick = { if (page < maxPage) page++ }, enabled = page < maxPage) { Text("Next") }
                }
            }
        }
    }
}

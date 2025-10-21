package com.example.project1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.app.Activity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1Theme {
                ResultsScreen()
            }
        }
    }
}

@Composable
fun ResultsScreen() {
    val context = LocalContext.current
    val activity = context as Activity

    val searchTerm = activity.intent.getStringExtra("searchTerm") ?: ""
    val selectedSource = activity.intent.getStringExtra("selectedSource")
    val titleText = if (!selectedSource.isNullOrEmpty()) {
        "$selectedSource results for \"$searchTerm\""
    } else {
        "Results for \"$searchTerm\""
    }

    val articlesManager = remember { ArticlesManager() }
    var articles by remember { mutableStateOf<List<NewsArticle>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    val category = null
    val apiKey = context.getString(R.string.NewsKey)

    LaunchedEffect(searchTerm, selectedSource) {
        isLoading = true
        errorMessage = null
        try {
            val result = withContext(Dispatchers.IO) {
                articlesManager.retrieveArticles(apiKey, searchTerm, selectedSource)
            }
            articles = result
            if (articles.isEmpty()) {
                errorMessage = "No articles found"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "Error fetching articles"
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(titleText, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            errorMessage != null -> {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(articles) { article ->
                        ArticleCard(article)
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleCard(article: NewsArticle) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                context.startActivity(intent)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            article.imageUrl?.takeIf { it.isNotBlank() }?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Article Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(article.title, style = MaterialTheme.typography.titleMedium)
            Text("Source: ${article.sourceName}", style = MaterialTheme.typography.bodySmall)

            article.description?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

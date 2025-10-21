package com.example.project1

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.project1.ui.theme.Project1Theme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import android.location.Geocoder

class MapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Project1Theme {
                MapScreen()
            }
        }
    }
}

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val apiKey = context.getString(R.string.NewsKey)
    val articlesManager = remember { ArticlesManager() }
    val coroutineScope = rememberCoroutineScope()

    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var locationName by remember { mutableStateOf("Long press on the map") }
    var articles by remember { mutableStateOf<List<NewsArticle>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val defaultLocation = LatLng(20.0, 0.0)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 2f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLongClick = { latLng ->
                markerPosition = latLng
                coroutineScope.launch {
                    isLoading = true

                    val address = getAddressGeocodeCurrent(context, latLng)
                    locationName = address

                    if (address.contains("Unknown", ignoreCase = true)) {
                        articles = emptyList()
                        Toast.makeText(context, "No news found for this location.", Toast.LENGTH_SHORT).show()
                    } else {
                        val fetched = withContext(Dispatchers.IO) {
                            articlesManager.fetchLocalNews(apiKey, address)
                        }
                        articles = fetched
                        if (fetched.isEmpty()) {
                            Toast.makeText(context, "No articles found for $address.", Toast.LENGTH_SHORT).show()
                        }
                        // Move camera to marker
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 6f))
                    }

                    isLoading = false
                }
            }
        ) {
            markerPosition?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Results for $locationName"
                )
            }
        }

        // Overlay UI
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(Color(0xCCFFFFFF))
                .padding(12.dp)
        ) {
            Text(
                text = "Results for: $locationName",
                style = MaterialTheme.typography.titleMedium
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (articles.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(articles) { article ->
                        ArticleCard(article)
                    }
                }
            } else if (!locationName.equals("Long press on the map")) {
                Text("No news found for this location.", color = Color.Gray)
            }
        }
    }
}

suspend fun getAddressGeocodeCurrent(context: Context, latLng: LatLng): String =
    withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val results = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            }

            if (!results.isNullOrEmpty()) {
                val address = results[0]
                val city = address.locality ?: ""
                val state = address.adminArea ?: ""
                val country = address.countryName ?: ""

                val fullAddress = listOf(city, state, country)
                    .filter { it.isNotBlank() }
                    .joinToString(", ")

                val parts = fullAddress.split(",").map { it.trim() }
                when {
                    parts.size >= 2 -> parts[1] // prefer state
                    parts.isNotEmpty() -> parts[0] // then city
                    else -> "Unknown location"
                }
            } else "Unknown location"
        } catch (e: Exception) {
            e.printStackTrace()
            "Unknown location"
        }
    }

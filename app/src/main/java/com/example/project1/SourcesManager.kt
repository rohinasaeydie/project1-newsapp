package com.example.project1

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class SourcesManager {
    private val client: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(logging)
        client = builder.build()
    }

    fun retrieveSources(apiKey: String, category: String? = null): List<NewsSource> {
        val urlBuilder = StringBuilder("https://newsapi.org/v2/top-headlines/sources")
        if (category != null && category != "All") {
            urlBuilder.append("?category=${category.lowercase()}")
        }

        val request = Request.Builder()
            .url(urlBuilder.toString())
            .header("X-Api-Key", apiKey)
            .get()
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string()

        if (!response.isSuccessful || body.isNullOrEmpty()) return emptyList()

        val json = JSONObject(body)
        val sourcesArray = json.getJSONArray("sources")
        val list = mutableListOf<NewsSource>()

        for (i in 0 until sourcesArray.length()) {
            val item = sourcesArray.getJSONObject(i)
            val id = item.optString("id")
            val name = item.optString("name")
            val description = item.optString("description")
            val url = item.optString("url")
            val categoryStr = item.optString("category")
            val country = item.optString("country")

            list.add(
                NewsSource(
                    id = id,
                    name = name,
                    description = description,
                    url = url,
                    category = categoryStr,
                    country = country
                )
            )
        }

        return list
    }
}

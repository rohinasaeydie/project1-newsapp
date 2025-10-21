package com.example.project1

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.net.URLEncoder

class ArticlesManager {

    private val client: OkHttpClient

    var totalResults: Int = 0
        private set

    init {
        val builder = OkHttpClient.Builder()

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(logging)

        client = builder.build()
    }

    fun getTopHeadlines(
        apiKey: String,
        category: String = "general",
        page: Int = 1
    ): List<NewsArticle> {
        val url =
            "https://newsapi.org/v2/top-headlines?category=${category.lowercase()}&page=$page&apiKey=$apiKey"

        val request = Request.Builder().url(url).get().build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return emptyList()

        if (!response.isSuccessful) return emptyList()

        val json = JSONObject(body)
        totalResults = json.optInt("totalResults", 0)

        val articlesArray = json.optJSONArray("articles") ?: return emptyList()
        val articles = mutableListOf<NewsArticle>()

        for (i in 0 until articlesArray.length()) {
            val item = articlesArray.getJSONObject(i)
            val sourceName = item.getJSONObject("source").optString("name")
            val title = item.optString("title")
            val description = item.optString("description", null)
            val urlLink = item.optString("url")
            val imageUrl = item.optString("urlToImage", null)

            articles.add(
                NewsArticle(
                    title = title,
                    sourceName = sourceName,
                    description = description,
                    url = urlLink,
                    imageUrl = imageUrl
                )
            )
        }

        return articles
    }

    fun retrieveArticles(
        apiKey: String,
        searchTerm: String,
        source: String? = null,
        page: Int = 1
    ): List<NewsArticle> {
        val urlBuilder =
            StringBuilder("https://newsapi.org/v2/everything?q=${searchTerm}&page=$page&apiKey=$apiKey")
        if (!source.isNullOrEmpty()) {
            urlBuilder.append("&sources=$source")
        }

        val request = Request.Builder().url(urlBuilder.toString()).get().build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return emptyList()

        if (!response.isSuccessful) return emptyList()

        val json = JSONObject(body)
        totalResults = json.optInt("totalResults", 0)
        val articlesArray = json.optJSONArray("articles") ?: return emptyList()

        val articles = mutableListOf<NewsArticle>()
        for (i in 0 until articlesArray.length()) {
            val item = articlesArray.getJSONObject(i)
            val sourceName = item.getJSONObject("source").optString("name")
            val title = item.optString("title")
            val description = item.optString("description", null)
            val urlLink = item.optString("url")
            val imageUrl = item.optString("urlToImage", null)

            articles.add(
                NewsArticle(
                    title = title,
                    sourceName = sourceName,
                    description = description,
                    url = urlLink,
                    imageUrl = imageUrl
                )
            )
        }

        return articles
    }


    suspend fun fetchLocalNews(apiKey: String, locationName: String): List<NewsArticle> {
        val encodedQuery = URLEncoder.encode(locationName, "UTF-8")
        val url =
            "https://newsapi.org/v2/everything?q=$encodedQuery&pageSize=10&apiKey=$apiKey"
        val request = Request.Builder().url(url).get().build()

        return try {
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return emptyList()

            if (!response.isSuccessful) {
                return emptyList()
            }

            val json = JSONObject(body)
            val total = json.optInt("totalResults", 0)

            val articlesArray = json.optJSONArray("articles") ?: return emptyList()
            val articles = mutableListOf<NewsArticle>()

            for (i in 0 until articlesArray.length()) {
                val item = articlesArray.getJSONObject(i)
                val sourceName = item.getJSONObject("source").optString("name")
                val title = item.optString("title")
                val description = item.optString("description", null)
                val urlLink = item.optString("url")
                val imageUrl = item.optString("urlToImage", null)

                articles.add(
                    NewsArticle(
                        title = title,
                        sourceName = sourceName,
                        description = description,
                        url = urlLink,
                        imageUrl = imageUrl
                    )
                )
            }

            articles
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

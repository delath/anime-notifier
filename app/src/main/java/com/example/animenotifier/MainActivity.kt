package com.example.animenotifier

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private val gson = Gson()
    private var mediaList: MutableList<JsonElement> = mutableListOf()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchAllAnimeList(getCurrentSeason())
    }

    private fun fetchAnimeListForSeason(season: String, page: Int = 1) {
        val request = Request.Builder()
            .url("https://graphql.anilist.co")
            .post(
                """
                {
                    "query": "{ Page(page: $page, perPage: 50) { media(status: RELEASING, season: $season, type: ANIME, isAdult: false, sort: POPULARITY_DESC) { title { romaji } coverImage { large } } } }"
                }
            """.trimIndent().toRequestBody("application/json; charset=utf-8".toMediaType())
            )
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.e("MainActivity", "Unexpected code $response")
                return
            }

            val body = response.body

            if (body != null) {
                val jsonResponse = gson.fromJson(body.string(), JsonObject::class.java)
                val media = jsonResponse.getAsJsonObject("data").getAsJsonObject("Page").getAsJsonArray("media")

                synchronized(this) {
                    mediaList.addAll(media)
                }

                // If we got 50 entries, there might be more. Try fetching the next page.
                if (media.size() == 50) {
                    fetchAnimeListForSeason(season, page + 1)
                } else {
                    Log.i("MainActivity", "No more pages for this season")
                }
            } else {
                Log.e("MainActivity", "Response body is null")
            }
        }
    }

    private fun fetchAllAnimeList(currentSeason: String) {
        val allSeasons = listOf("SPRING", "SUMMER", "FALL", "WINTER")
        val sortedSeasons = listOf(currentSeason) + allSeasons.filter { it != currentSeason }

        scope.launch {
            sortedSeasons.forEach { season ->
                fetchAnimeListForSeason(season)
            }

            withContext(Dispatchers.Main) {
                updateUI()
            }
        }
    }

    private fun updateUI() {
        val animeList = mediaList.map {
            it.asJsonObject.let { anime ->
                Anime(
                    title = anime["title"].asJsonObject["romaji"].asString,
                    imageUrl = anime["coverImage"].asJsonObject["large"].asString
                )
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = AnimeAdapter(animeList)
    }

    private fun getCurrentSeason(): String {
        val month = Calendar.getInstance().get(Calendar.MONTH)
        return when (month) {
            in Calendar.MARCH until Calendar.JUNE -> "SPRING"
            in Calendar.JUNE until Calendar.SEPTEMBER -> "SUMMER"
            in Calendar.SEPTEMBER until Calendar.DECEMBER -> "FALL"
            else -> "WINTER"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

}

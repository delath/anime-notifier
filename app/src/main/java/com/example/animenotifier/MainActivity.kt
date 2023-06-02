package com.example.animenotifier

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var animeDao: AnimeDao
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private lateinit var adapter: AnimeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "anime-database"
        ).build()

        //animeDao = db.animeDao()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val animeDao: AnimeDao = db.animeDao()

        adapter = AnimeAdapter(listOf()) { anime ->
            anime.isBookmarked = !anime.isBookmarked
            uiScope.launch {
                animeDao.updateAnime(anime)
                adapter.updateList(animeDao.getAnimes())
            }
        }

        recyclerView.adapter = adapter

        uiScope.launch {
            val animeList = getAnimeList()
            animeDao.insertAnimes(animeList)
            adapter.updateList(animeList)
        }
    }

    private suspend fun getAnimeList(): List<Anime> {
        return withContext(Dispatchers.IO) {
            val season = getCurrentSeason()
            val animeList = fetchAllAnimeList(season)
            val dbAnimeList = animeDao.getAnimes()

            // Merge the fetched list and db list
            for (anime in animeList) {
                for (dbAnime in dbAnimeList) {
                    if (anime.title == dbAnime.title) {
                        anime.isBookmarked = dbAnime.isBookmarked
                        break
                    }
                }
            }
            animeList
        }
    }

    private fun getCurrentSeason(): String {
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1
        return when (month) {
            in 1..3 -> "winter"
            in 4..6 -> "spring"
            in 7..9 -> "summer"
            in 10..12 -> "fall"
            else -> "winter"
        }
    }

    private suspend fun fetchAllAnimeList(season: String): List<Anime> {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val mediaType = "application/json".toMediaType()
            val requestBody = "{season:\"$season\", year:2023}".toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://api.aniapi.com/v1/anime")
                .post(requestBody)
                .build()

            var animeList = listOf<Anime>()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()
                val jsonObject = JSONObject(body)
                val data = jsonObject.getJSONObject("data").getJSONArray("documents")

                animeList = (0 until data.length()).map { i ->
                    val item = data.getJSONObject(i)
                    Anime(
                        item.getString("titles"),
                        item.getString("cover_image"),
                        false // initially no anime is bookmarked
                    )
                }
            }
            animeList
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}
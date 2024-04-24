package eu.delath.animenotifier.ui.all

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import eu.delath.animenotifier.model.Anime
import eu.delath.animenotifier.network.AnilistApiService
import eu.delath.animenotifier.network.AnilistResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnimeRepository(private val apiService: AnilistApiService) {
    private val _animeList = MutableLiveData<List<Anime>>()
    val animeList: LiveData<List<Anime>> = _animeList

    fun fetchCurrentSeasonAnime() {
        val query = "{\"query\":\"{ Page(page: 1, perPage: 50) { media(season: SUMMER, seasonYear: 2021, type: ANIME) { id title { romaji } coverImage { extraLarge } episodes } } }\"}"
        apiService.fetchCurrentSeasonAnime(query).enqueue(object : Callback<AnilistResponse> {
            override fun onResponse(call: Call<AnilistResponse>, response: Response<AnilistResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.page?.media?.let {
                        _animeList.postValue(it.map { media ->
                            Anime(
                                id = media.id.toString(),
                                title = media.title.romaji,
                                imageUrl = media.coverImage.extraLarge,
                                episodeCount = media.episodes ?: 0
                            )
                        })
                    }
                } else {
                    Log.e("API Error", "Response not successful")
                }
            }

            override fun onFailure(call: Call<AnilistResponse>, t: Throwable) {
                Log.e("API Error", "Network request failed: ${t.message}")
            }
        })
    }
}

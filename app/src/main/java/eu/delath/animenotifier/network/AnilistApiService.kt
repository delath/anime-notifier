package eu.delath.animenotifier.network

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface AnilistApiService {
    @Headers("Content-Type: application/json")
    @POST("/")
    fun fetchCurrentSeasonAnime(@Body query: String): Call<AnilistResponse>

    companion object {
        private const val BASE_URL = "https://graphql.anilist.co"

        fun create(): AnilistApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AnilistApiService::class.java)
        }
    }
}

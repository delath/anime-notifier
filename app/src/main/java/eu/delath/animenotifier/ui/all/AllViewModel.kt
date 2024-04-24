package eu.delath.animenotifier.ui.all

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import eu.delath.animenotifier.model.Anime
import eu.delath.animenotifier.network.AnilistApiService

class AllViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AnimeRepository
    val animes: LiveData<List<Anime>>

    init {
        val animeService = AnilistApiService.create()
        repository = AnimeRepository(animeService)
        animes = repository.animeList
    }
}

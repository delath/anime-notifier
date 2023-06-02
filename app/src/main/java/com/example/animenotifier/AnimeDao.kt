package com.example.animenotifier

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AnimeDao {
    @Query("SELECT * FROM Anime")
    suspend fun getAnimes(): List<Anime>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimes(animeList: List<Anime>)

    @Update
    suspend fun updateAnime(anime: Anime)
}
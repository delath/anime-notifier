package com.example.animenotifier

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AnimeEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val imageUrl: String,
    val nextEpisodeReleaseDate: Long,
    val isBookmarked: Boolean,
    val lastUpdated: Long
)
package com.example.animenotifier

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Anime(
    @PrimaryKey val title: String,
    val imageUrl: String,
    var isBookmarked: Boolean = false
)

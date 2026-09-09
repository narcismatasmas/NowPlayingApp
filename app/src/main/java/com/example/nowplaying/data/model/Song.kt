package com.example.nowplaying.data.model

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class Song (
    val success: Boolean = false,
    val device_id: String = "default",
    val title: String = "Unknown",
    val artist: String = "Unknown",
    val album_art: String = "https://placehold.net/400x400.png",
    @SerializedName(value = "timestamp", alternate = ["detected_at"])
    val timestamp: String = "Unknown"
)
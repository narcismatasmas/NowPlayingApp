package com.example.nowplaying.data.model

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class Song (
    val id: Int = 0,
    val success: Boolean = false,
    val device_id: String = "default",
    val title: String = "Unknown",
    val artist: String = "Unknown",
    val album_art: String = "https://images.unsplash.com/photo-1652626627230-d7ddb64bf9e9?q=80&w=880&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    @SerializedName(value = "timestamp", alternate = ["detected_at"])
    val timestamp: String = "Unknown"
)
package com.example.nowplaying.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object MediaListRoute

@Serializable
data class DetailRoute(
    val source: String
)
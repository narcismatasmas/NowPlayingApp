package com.example.nowplaying.data.network

import com.example.nowplaying.data.model.Song
import com.example.nowplaying.data.model.SongList
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://192.168.1.137:8000/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface ApiInterface {
    @GET("now-playing")
    suspend fun getNowPlaying(
        @Query("device_id") device_id: String
    ): Song

    @GET("history")
    suspend fun getHistory(
    ): SongList
}

object ApiRequester {
    val retrofitService: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}

package com.example.nowplaying.data.network

import com.example.nowplaying.data.model.Song
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://192.168.1.137:8000/"
private val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY // Esto mostrará TODO: URL, headers y cuerpo
}
private val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(client)
    .build()

interface ApiInterface {
    @GET("now-playing")
    suspend fun getNowPlaying(
        @Query("device_id") device_id: String
    ): Song

    @GET("history")
    suspend fun getHistory(
        @Query("device_id") device_id: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): List<Song>
}

object ApiRequester {
    val retrofitService: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}

package com.example.nowplaying.data.network

import com.example.nowplaying.data.model.Song
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://192.168.1.137:8000/"
private const val VPN_URL = "http://100.116.31.29:8000/"

class FallbackUrlInterceptor(private val fallbackBaseUrl: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return try {
            chain.proceed(request)
        } catch (e: IOException) {
            val fallbackUrl = fallbackBaseUrl.toHttpUrlOrNull() ?: throw e
            val newUrl = request.url.newBuilder()
                .scheme(fallbackUrl.scheme)
                .host(fallbackUrl.host)
                .port(fallbackUrl.port)
                .build()
            
            chain.proceed(request.newBuilder().url(newUrl).build())
        }
    }
}

private val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY // Esto mostrará TODO: URL, headers y cuerpo
}
private val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .addInterceptor(FallbackUrlInterceptor(VPN_URL))
    .connectTimeout(3, TimeUnit.SECONDS) // IMPORTANTE: 3 segundos para que no se quede colgado cargando en la calle
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

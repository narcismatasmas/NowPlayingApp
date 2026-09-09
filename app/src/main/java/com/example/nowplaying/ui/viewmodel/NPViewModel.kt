package com.example.nowplaying.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.nowplaying.data.model.Song
import com.example.nowplaying.data.network.ApiRequester
import com.example.nowplaying.data.paging.SongPagingSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class NPViewModel: ViewModel() {

    private val _currentSong = MutableStateFlow<Song?>(null)

    val currentSong: StateFlow<Song?> = _currentSong

    val historyFlow: Flow<PagingData<Song>> = Pager(
        config = PagingConfig(
            pageSize = 25, // Cuántos elementos cargar por página
            prefetchDistance = 5, // A falta de cuántos elementos para el final empezar a cargar la siguiente
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SongPagingSource(ApiRequester.retrofitService, "RaspberryBCN")
        }
    ).flow.cachedIn(viewModelScope) // Fundamental para que no se pierdan los datos al rotar la pantalla

    fun startPolling(devce_id: String) {
        viewModelScope.launch {
            while (true) {
                try {
                    val song = ApiRequester.retrofitService.getNowPlaying(devce_id)
                    _currentSong.value = song
                    Log.d("API", "Canción actualizada: ${song.title}")
                } catch (e: Exception){
                    Log.e("Error", "Fallo al obtener canción: ${e.message}")
                }

                delay(1000.milliseconds)
            }

        }
    }



}
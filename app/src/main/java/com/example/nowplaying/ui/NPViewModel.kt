package com.example.nowplaying.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nowplaying.data.model.Song
import com.example.nowplaying.data.network.ApiRequester
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class NPViewModel: ViewModel() {

    private val _currentSong = MutableStateFlow<Song?> (null)

    val currentSong: StateFlow<Song?> = _currentSong
    fun startPolling(devce_id: String) {
        viewModelScope.launch{
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
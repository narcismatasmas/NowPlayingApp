package com.example.nowplaying

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nowplaying.ui.viewmodel.NPViewModel
import com.example.nowplaying.ui.features.list.MediaListScreen
import com.example.nowplaying.ui.features.nowplaying.NowPlayingBar
import com.example.nowplaying.ui.theme.NowPlayingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NowPlayingTheme {
                val navController = rememberNavController()

                val sharedViewModel: NPViewModel = viewModel()

                LaunchedEffect(Unit) {
                    sharedViewModel.startPolling("RaspberryBCN")
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)){
                        NavHost(
                            navController = navController,
                            startDestination = "list",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("list"){
                                MediaListScreen()

                            }
                        }

                        NowPlayingBar(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            viewModel = sharedViewModel
                        )
                    }


                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MediaListPreview() {
    NowPlayingTheme {
        MediaListScreen()
    }
}


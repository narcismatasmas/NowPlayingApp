package com.example.nowplaying

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.nowplaying.ui.features.details.DetailScreen
import com.example.nowplaying.ui.viewmodel.NPViewModel
import com.example.nowplaying.ui.features.list.MediaListScreen
import com.example.nowplaying.ui.features.nowplaying.NowPlayingBar
import com.example.nowplaying.ui.navigation.DetailRoute
import com.example.nowplaying.ui.navigation.MediaListRoute
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

                    Box(modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())){
                        SharedTransitionLayout {
                            NavHost(
                                navController = navController,
                                startDestination = MediaListRoute,
                                modifier = Modifier,
                                enterTransition = { fadeIn(animationSpec = tween(400))},
                                exitTransition = { fadeOut(animationSpec = tween(400))},
                                popEnterTransition = { fadeIn(animationSpec = tween(400))},
                                popExitTransition = { fadeOut(animationSpec = tween(400))},
                                predictivePopExitTransition = { fadeOut(animationSpec = tween(400))},
                                predictivePopEnterTransition = { fadeIn(animationSpec = tween(400))},
                                sizeTransform = {androidx.compose.animation.SizeTransform(clip = false)}
                            ) {
                                /*composable("list"){
                                    MediaListScreen(viewModel = sharedViewModel)

                                }*/
                                composable<MediaListRoute>{
                                    MediaListScreen(
                                        navController = navController,
                                        viewModel = sharedViewModel,
                                        sharedTransitionScope = this@SharedTransitionLayout,
                                        animatedVisibilityScope = this@composable)

                                    val currentSong by sharedViewModel.currentSong.collectAsState()
                                    if (currentSong != null) {
                                        NowPlayingBar(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .padding(16.dp),
                                            viewModel = sharedViewModel,
                                            navController = navController,
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            animatedVisibilityScope = this@composable
                                        )
                                    }
                                }
                                composable<DetailRoute>{ backStackEntry ->
                                    val routeParams = backStackEntry.toRoute<DetailRoute>()
                                    DetailScreen(
                                        navController = navController,
                                        viewModel = sharedViewModel,
                                        source = routeParams.source,
                                        sharedTransitionScope = this@SharedTransitionLayout,
                                        animatedVisibilityScope = this@composable
                                        )
                                }
                            }
                        }





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
        //MediaListScreen()
    }
}


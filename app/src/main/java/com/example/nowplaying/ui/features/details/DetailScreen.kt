package com.example.nowplaying.ui.features.details

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.nowplaying.data.model.Song
import com.example.nowplaying.ui.viewmodel.NPViewModel

@Composable
fun DetailScreen(
    navController: NavController? = null,
    viewModel: NPViewModel = viewModel(),
    modifier: Modifier = Modifier,
    source: String = "now_playing",
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val song by if (source == "now_playing") {
        viewModel.currentSong.collectAsState()
    } else {
        viewModel.selectedHistorySong.collectAsState()
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
                .windowInsetsTopHeight(WindowInsets.statusBars)
        )

        Card(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
            colors = CardColors(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Now Playing",
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        fontWeight = FontWeight.Bold,
                    )
                )
            }
        }

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center
            ) {
                with(sharedTransitionScope) {
                    AsyncImage(
                        model = song?.album_art ?: run {
                            Song().album_art
                        },
                        contentDescription = "Album Art",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = "album_art_${song?.id}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ -> tween(400) }
                            )
                            .clip(RoundedCornerShape(16.dp))
                        ,
                        contentScale = ContentScale.Fit
                    )
                }
                with(sharedTransitionScope) {
                    Text(
                        text = song?.title ?: run {
                            Song().title
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = modifier
                            .padding(top = 8.dp, bottom = 0.dp)
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = "title_${song?.id}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ -> tween(400) }
                            )
                    )
                }
                with(sharedTransitionScope) {
                    Text(
                        text = song?.artist ?: run {
                            Song().artist
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Light
                        ),
                        modifier = modifier
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = "artist_${song?.id}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ -> tween(400) }
                            )
                    )
                }
            }



    }

}
package com.example.nowplaying.ui.features.nowplaying

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.nowplaying.ui.viewmodel.NPViewModel
import com.example.nowplaying.ui.components.fadedEdge

@Composable
fun NowPlayingBar(
    modifier: Modifier = Modifier,
    viewModel: NPViewModel = viewModel()
){
    val song by viewModel.currentSong.collectAsState()
    var isTitleOverflow by remember { mutableStateOf(false) }
    var isArtistOverflow by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardColors(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary, MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = song?.album_art ?: run {
                    "https://fastly.picsum.photos/id/964/200/200.jpg?hmac=Xpyys0fUK6t9mTJx-ZmJH2T9G2Hp7bfieNlb-dHqBek"
                },
                contentDescription = "Album Art",
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 16.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier.weight(1f).padding(end = 16.dp)
            ) {
                Text(
                    text = song?.title ?: run {
                        "Unknown/Undetected"
                    },
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Clip, // Importante: Clip en lugar de Ellipsis
                    onTextLayout = { textLayoutResult ->
                        isTitleOverflow = textLayoutResult.hasVisualOverflow
                    },
                    modifier = Modifier.fadedEdge(
                        brush = Brush.horizontalGradient(
                            0.85f to Color.Black, // Opaco hasta el 85%
                            1f to Color.Transparent // Transparente al 100%
                        ),
                        isVisible = isTitleOverflow
                    )
                )
                Text(
                    text = song?.artist ?: run {
                        "Unknown/Undetected"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Light
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Clip, // Importante: Clip en lugar de Ellipsis
                    onTextLayout = { textLayoutResult ->
                        isArtistOverflow = textLayoutResult.hasVisualOverflow
                    },
                    modifier = Modifier.fadedEdge(
                        brush = Brush.horizontalGradient(
                            0.85f to Color.Black, // Opaco hasta el 85%
                            1f to Color.Transparent // Transparente al 100%
                        ),
                        isVisible = isArtistOverflow
                    )
                )
            }

            IconButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)                ,
                shape = RoundedCornerShape(8.dp),
                onClick = { /*TODO*/ },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInFull,
                    contentDescription = "Expand"
                )
            }
        }
    }
}
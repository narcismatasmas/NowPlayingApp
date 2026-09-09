package com.example.nowplaying.ui.features.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.nowplaying.data.model.Song
import com.example.nowplaying.ui.NPViewModel
import com.example.nowplaying.ui.components.fadedEdge


val hardcodedList = listOf(
    Song(),
    Song(),
    Song()
)

@Composable
fun MediaListScreen(
    navController: NavController? = null,
    viewModel: NPViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(hardcodedList) { item ->
            MediaListItem(item)
        }
    }
}

@Composable
fun MediaListItem(item: Song) {
    var isTitleOverflow by remember { mutableStateOf(false) }
    var isArtistOverflow by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardColors(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface, MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = item.album_art,
                contentDescription = "Album Art",
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 16.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
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
                    text = item.artist,
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
        }
    }
}


package com.example.nowplaying.ui.features.list

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.example.nowplaying.data.model.Song
import com.example.nowplaying.ui.viewmodel.NPViewModel
import com.example.nowplaying.ui.components.fadedEdge
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.paging.LoadState
import com.example.nowplaying.ui.navigation.DetailRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun formatTimeStamp(timestamp: String): String {
    return try {
        val odt = OffsetDateTime.parse(timestamp)
        val localDateTime = odt.atZoneSameInstant(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm")
        localDateTime.format(formatter)
    } catch (e: Exception){
        timestamp
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaListScreen(
    navController: NavController? = null,
    viewModel: NPViewModel = viewModel(),
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val coroutineScope = rememberCoroutineScope()

    val listState = rememberLazyListState()

    val historyItems = viewModel.historyFlow.collectAsLazyPagingItems()

    var isRefreshing by remember {mutableStateOf(false)}
    val pullToRefreshState = rememberPullToRefreshState()

    // 1. Guardamos la altura MÁXIMA que necesita el título en píxeles.
    // Empezamos en 0 y la mediremos después.
    var maxTitleHeightPx by rememberSaveable { mutableStateOf(0f) }
    // Este es el multiplicador. A 0.5f, la cabecera se encoge a la mitad de velocidad que la lista.

    // El offset actual de nuestra barra. 0 = visible entera. -maxTitleHeightPx = oculta entera.
    var currentHeightOffsetPx by rememberSaveable { mutableStateOf(0f) }

    // --- CONFIGURACIÓN ---
    val scrollSpeedFactor = 0.5f

    // Píxeles (en densidad de pantalla) que hay que subir el dedo antes de que la barra empiece a asomar
    val deadzoneThresholdDp = 20.dp
    val density = LocalDensity.current
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density).toFloat()
    val deadzoneThresholdPx = with(density) { deadzoneThresholdDp.toPx() }

    // Acumulador temporal para saber si hemos pasado la zona muerta al subir
    var upwardScrollAccumulator by rememberSaveable { mutableStateOf(0f) }

    // Nuestro motor personalizado que lee el scroll desde cualquier punto de la lista
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                if (delta < 0) {
                    // 1. SCROLL HACIA ABAJO (Ocultar barra)
                    // Reseteamos el acumulador de subir porque estamos bajando
                    upwardScrollAccumulator = 0f

                    val scaledDelta = delta * scrollSpeedFactor
                    currentHeightOffsetPx = (currentHeightOffsetPx + scaledDelta).coerceIn(-(maxTitleHeightPx), 0f)

                } else if (delta > 0 && currentHeightOffsetPx < 0f) {
                    // 2. SCROLL HACIA ARRIBA (Mostrar barra) y la barra está parcial o totalmente oculta
                    upwardScrollAccumulator += delta

                    // Solo empezamos a mostrar la barra si hemos subido el dedo más que la zona muerta
                    if (upwardScrollAccumulator > deadzoneThresholdPx) {
                        // Al delta le restamos lo que ya consumió la zona muerta para que el inicio sea suave
                        val activeDelta = delta * scrollSpeedFactor
                        currentHeightOffsetPx = (currentHeightOffsetPx + activeDelta).coerceIn(-(maxTitleHeightPx), 0f)
                    }
                } else if (delta > 0 && currentHeightOffsetPx == 0f) {
                    // Si hacemos scroll hacia arriba y la barra YA está 100% visible, no hacemos nada extra.
                    upwardScrollAccumulator += delta
                }

                // ¡LA CLAVE ESTÁ AQUÍ!
                // Devolvemos Offset.Zero SIEMPRE.
                // Esto le dice a Android: "La cabecera ha leído los datos para su animación interna,
                // pero no he robado ningún píxel. Dale a la lista el 100% de la velocidad".
                return Offset.Zero
            }
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        val currentHeightDp = with(density) {
            (maxTitleHeightPx + currentHeightOffsetPx).coerceAtLeast(0f).toDp()
        }

        // Calculamos la opacidad:
        // Si maxTitleHeightPx es 0 (aún no se ha medido), la opacidad es 1f (100%).
        // Si ya se ha medido, la opacidad es: (Altura Máxima - Lo que hemos ocultado) / Altura Máxima.
        // Ejemplo: Si medía 40px y hemos ocultado 20px, la opacidad será (40 - 20) / 40 = 0.5f (50% transparente).
        val titleAlpha = if (maxTitleHeightPx > 0f) {
            // 1. Calculamos el porcentaje normal de apertura (de 0.0 a 1.0)
            val expansionRatio = (maxTitleHeightPx + currentHeightOffsetPx) / maxTitleHeightPx

            // 2. Aceleramos el desvanecimiento:
            // Si el ratio es 1.0 (abierto del todo): (1.0 * 2) - 1 = 1.0 (Totalmente visible)
            // Si el ratio es 0.75 (3/4 abierto): (0.75 * 2) - 1 = 0.5 (Mitad transparente)
            // Si el ratio es 0.5 (medio abierto): (0.5 * 2) - 1 = 0.0 (Totalmente invisible)
            // Si baja de 0.5, coerceIn(0f, 1f) se asegura de que no haya alphas negativos.
            ((expansionRatio * 2f) - 1f).coerceIn(0f, 1f)
        } else {
            1f
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
                .windowInsetsTopHeight(WindowInsets.statusBars)
        )

        Box(
            modifier = Modifier
                .then(
                    if(maxTitleHeightPx == 0f) {
                        Modifier.wrapContentHeight()
                    } else {
                        Modifier.height(currentHeightDp)
                    }
                )
                .onSizeChanged { size ->
                    if (maxTitleHeightPx == 0f) {
                        maxTitleHeightPx = size.height.toFloat()
                    }
                }
                .clipToBounds(),
            contentAlignment = Alignment.TopStart
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                colors = CardColors(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
            ) {
                // Cambiamos el Text único por una Column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp, end = 16.dp)
                            .fillMaxWidth()
                            .alpha(titleAlpha),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            // Mantenemos solo paddings laterales y superior. NADA por abajo.
                            text = "Historial",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                                fontWeight = FontWeight.Bold,
                            )
                        )
                    }

                    // Este Spacer actúa como tu padding inferior.
                    // Al encogerse el Box padre, esto será lo primero que se recorte de forma natural.
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        val currentSong by viewModel.currentSong.collectAsState()

        val targetFadeStart = if (currentSong != null) 0.80f else 0.90f
        val targetFadeEnd = if (currentSong != null) 0.95f else 1f

        val animatedFadeStart by animateFloatAsState(
            targetValue = targetFadeStart,
            animationSpec = tween(500),
            label = "FadedStartAnimated"
        )

        val animatedFadeEnd by animateFloatAsState(
            targetValue = targetFadeEnd,
            animationSpec = tween(500),
            label = "FadedEndAnimated"
        )

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing = true

                    currentHeightOffsetPx = 0f
                    upwardScrollAccumulator = 0f

                    historyItems.refresh()
                    delay(500)
                    isRefreshing = false
                }
            },
            state = pullToRefreshState,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                .clip(shape = RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp))
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .fadedEdge(brush = Brush.verticalGradient(
                        animatedFadeStart to Color.Black, // Opaco hasta el 85%
                        animatedFadeEnd to Color.Transparent // Transparente al 100%
                    )
                    )
            ) {
                items(
                    count = historyItems.itemCount,
                    key = {index -> historyItems[index]?.id ?: index }
                ) { index ->
                    val item = historyItems[index]
                    if (item != null) {
                        MediaListItem(item, navController, viewModel, sharedTransitionScope, animatedVisibilityScope)
                    } else {
                        MediaListItem(Song(), navController, viewModel, sharedTransitionScope, animatedVisibilityScope)
                    }
                }

                // Elemento extra al final para compensar el difuminado
                item {
                    Spacer(modifier = Modifier.height(100.dp)) // Ajusta esta altura según lo que necesites
                }
            }
        }

    }
}

@Composable
fun MediaListItem(
    item: Song,
    navController: NavController? = null,
    viewModel: NPViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var isTitleOverflow by remember { mutableStateOf(false) }
    var isArtistOverflow by remember { mutableStateOf(false) }


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .padding(bottom = 4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardColors(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface, MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface),
            onClick = {
                viewModel.selectHistorySong(item)
                navController?.navigate(route = DetailRoute(source = "detail"))
            }
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                with(sharedTransitionScope) {
                    AsyncImage(
                        model = item.album_art,
                        contentDescription = "Album Art",
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = 16.dp)
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = "album_art_${item.id}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ -> tween(400) }
                            )
                            .clip(RoundedCornerShape(16.dp))
                        ,
                        contentScale = ContentScale.Fit
                    )
                }
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    with(sharedTransitionScope) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip, // Importante: Clip en lugar de Ellipsis
                            onTextLayout = { textLayoutResult ->
                                isTitleOverflow = textLayoutResult.hasVisualOverflow
                            },
                            modifier = Modifier
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = "title_${item.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ -> tween(400) }
                                )
                                .fadedEdge(
                                    brush = Brush.horizontalGradient(
                                        0.85f to Color.Black, // Opaco hasta el 85%
                                        1f to Color.Transparent // Transparente al 100%
                                    ),
                                    isVisible = isTitleOverflow
                                )

                        )
                    }
                    with(sharedTransitionScope) {
                        Text(
                            text = item.artist,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Light
                            ),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip, // Importante: Clip en lugar de Ellipsis
                            onTextLayout = { textLayoutResult ->
                                isArtistOverflow = textLayoutResult.hasVisualOverflow
                            },
                            modifier = Modifier
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = "artist_${item.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ -> tween(400) }
                                )
                                .fadedEdge(
                                    brush = Brush.horizontalGradient(
                                        0.85f to Color.Black, // Opaco hasta el 85%
                                        1f to Color.Transparent // Transparente al 100%
                                    ),
                                    isVisible = isArtistOverflow
                                )

                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = formatTimeStamp(item.timestamp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Clip
                    )
                }
            }
        }


}


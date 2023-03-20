package com.giphy.sdk.uidemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.views.dialogview.GiphyDialogView
import com.giphy.sdk.ui.views.dialogview.setup
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.network.api.Constants
import com.giphy.sdk.ui.views.GPHMediaView
import com.giphy.sdk.uidemo.VideoPlayer.VideoCache
import kotlin.math.roundToInt

class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                val listState = rememberLazyListState()
                val feedItems = remember {
                    mutableStateListOf<Media>()
                }
                Feed(items = feedItems, listState = listState)
                LaunchedEffect(feedItems.size) {
                    if (feedItems.size > 0) {
                        listState.scrollToItem(index = feedItems.size - 1)
                    }
                }
                EmbeddedViewDemo {
                    feedItems.add(it)
                }
            }
        }
    }
}

@Composable
fun Feed(items: MutableList<Media>, listState: LazyListState) {

    Box {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(items) { index, item ->
                FeedItem(media = item, index = index)
            }
        }
    }
}

@Composable
fun FeedItem(media: Media, index: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = if (index % 2 != 0) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            AndroidView(factory = { ctx ->
                GPHMediaView(ctx).apply {
                    setMedia(media)
                }
            })
        }
    }
}

@Composable
fun EmbeddedViewDemo(onMediaSelected: (Media) -> Unit) {
    val YOUR_API_KEY = ""
    var showView by remember { mutableStateOf(false) }
    var offset by remember { mutableStateOf(0f) }
    val configuration = LocalConfiguration.current

    Box {
        Button(
            onClick = {
                offset = 0f
                showView = true
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Show Giphy")
        }

        AnimatedVisibility(
            visible = showView,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
            ),
            modifier = Modifier.offset {
                IntOffset(0, offset.roundToInt())
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalConfiguration.current.screenHeightDp.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                AndroidView(
                    factory = { ctx ->
                        VideoCache.initialize(ctx, 100 * 1024 * 1024)
                        Giphy.configure(ctx, YOUR_API_KEY, true)
                        val settings =
                            GPHSettings(theme = GPHTheme.Light, stickerColumnCount = 3)
                        val contentType = GPHContentType.gif
                        GiphyDialogView(ctx).apply {
                            setup(
                                settings.copy(selectedContentType = contentType),
                                videoPlayer = { playerView, repeatable, showCaptions ->
                                    VideoPlayerExoPlayer2181Impl(
                                        playerView,
                                        repeatable,
                                        showCaptions
                                    )
                                }
                            )
                            this.listener = object : GiphyDialogView.Listener {
                                override fun onGifSelected(
                                    media: Media,
                                    searchTerm: String?,
                                    selectedContentType: GPHContentType
                                ) {
                                    onMediaSelected(media)
                                    showView = false
                                }

                                override fun onDismissed(selectedContentType: GPHContentType) {
                                    Log.d("Giphy", "view dismissed")
                                }

                                override fun didSearchTerm(term: String) {
                                    Log.d("Giphy", "didSearchTerm: $term")
                                }

                                override fun onFocusSearch() {
                                    Log.d("Giphy", "onFocusSearch")
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(LocalConfiguration.current.screenHeightDp.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp)
                        .background(Color.Transparent)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onVerticalDrag = { change, dragAmount ->
                                    offset += change.positionChange().y
                                    if (offset > configuration.screenHeightDp.dp.toPx() * 0.6 || dragAmount > 50) {
                                        showView = false
                                    }
                                },
                                onDragEnd = {
                                    if (offset <= configuration.screenHeightDp.dp.toPx() * 0.6) {
                                        offset = 0f
                                    }
                                },
                                onDragCancel = {
                                    if (offset <= configuration.screenHeightDp.dp.toPx() * 0.6) {
                                        offset = 0f
                                    }
                                }
                            )
                        }
                )
            }
        }
    }
}

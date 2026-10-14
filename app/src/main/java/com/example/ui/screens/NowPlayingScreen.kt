package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Song
import com.example.ui.components.AlbumArtView
import com.example.ui.theme.OnPeachPrimary
import com.example.ui.theme.PeachPrimary
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SageSecondary
import com.example.ui.theme.TextHighContrast
import com.example.ui.theme.TextMediumContrast

@Composable
fun NowPlayingScreen(
    currentSong: Song?,
    isPlaying: Boolean,
    playbackPositionMs: Long,
    durationMs: Long,
    isShuffleEnabled: Boolean,
    repeatMode: Int,
    isFavorite: Boolean,
    sleepTimerText: String,
    onCollapse: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenSleepTimer: () -> Unit
) {
    BackHandler { onCollapse() }

    var isDraggingSlider by remember { mutableStateOf(false) }
    var sliderDragPosition by remember { mutableFloatStateOf(0f) }
    var showOverflowMenu by remember { mutableStateOf(false) }

    val safeDuration = durationMs.coerceAtLeast(1L)
    val displayPosition = if (isDraggingSlider) {
        (sliderDragPosition * safeDuration).toLong()
    } else {
        playbackPositionMs.coerceIn(0L, safeDuration)
    }

    // Monospace tabular formatted timestamps
    val elapsedSeconds = (displayPosition / 1000).coerceAtLeast(0)
    val elapsedMins = elapsedSeconds / 60
    val elapsedSecs = elapsedSeconds % 60
    val elapsedFormatted = "%d:%02d".format(elapsedMins, elapsedSecs)

    val remainingSeconds = ((safeDuration - displayPosition) / 1000).coerceAtLeast(0)
    val remMins = remainingSeconds / 60
    val remSecs = remainingSeconds % 60
    val remainingFormatted = "-%d:%02d".format(remMins, remSecs)

    val song = currentSong ?: Song(
        id = 0L,
        title = "No Track Selected",
        artist = "Select a song from library",
        album = "Sonora Player",
        durationMs = 0L,
        contentUri = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("now_playing_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .widthIn(max = 480.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onCollapse,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                        .testTag("now_playing_collapse_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = TextHighContrast,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = song.album.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    color = TextMediumContrast,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                )

                Box {
                    IconButton(
                        onClick = { showOverflowMenu = true },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f))
                            .testTag("now_playing_overflow_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = TextHighContrast,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showOverflowMenu,
                        onDismissRequest = { showOverflowMenu = false },
                        modifier = Modifier.background(Color(0xFF1A1A1E))
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Sleep Timer ($sleepTimerText)",
                                    color = TextHighContrast
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Timer,
                                    contentDescription = null,
                                    tint = PeachPrimary
                                )
                            },
                            onClick = {
                                showOverflowMenu = false
                                onOpenSleepTimer()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("View Queue", color = TextHighContrast) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                                    contentDescription = null,
                                    tint = SageSecondary
                                )
                            },
                            onClick = {
                                showOverflowMenu = false
                                onOpenQueue()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Hero Album Artwork Stage with Soft Warm Luminescence Aura
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .aspectRatio(1f)
                    .widthIn(max = 328.dp),
                contentAlignment = Alignment.Center
            ) {
                // Ambient Luminescence Backdrop
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.92f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    PeachPrimary.copy(alpha = 0.28f),
                                    Color(0xFFE07A5F).copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                        .blur(36.dp)
                )

                // Foreground Artwork
                AlbumArtView(
                    artUri = song.albumArtUri,
                    songTitle = song.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(24.dp, RoundedCornerShape(26.dp), spotColor = Color.Black)
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(26.dp)),
                    cornerRadius = 26.dp,
                    iconSize = 64.dp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Track Metadata & Heart Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextHighContrast,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${song.artist} • ${song.album}",
                        fontSize = 14.sp,
                        color = TextMediumContrast,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("now_playing_fav_btn")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Favorite" else "Not favorite",
                        tint = if (isFavorite) PeachPrimary else TextMediumContrast,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Ultra-Clean Expressive Scrubber
            Column(modifier = Modifier.fillMaxWidth()) {
                val sliderValue = if (isDraggingSlider) {
                    sliderDragPosition
                } else {
                    (playbackPositionMs.toFloat() / safeDuration.toFloat()).coerceIn(0f, 1f)
                }

                Slider(
                    value = sliderValue,
                    onValueChange = { frac ->
                        isDraggingSlider = true
                        sliderDragPosition = frac
                    },
                    onValueChangeFinished = {
                        isDraggingSlider = false
                        onSeekTo((sliderDragPosition * safeDuration).toLong())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = PeachPrimary,
                        activeTrackColor = PeachPrimary,
                        inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("now_playing_scrubber")
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = elapsedFormatted,
                        fontSize = 12.sp,
                        color = TextMediumContrast,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = remainingFormatted,
                        fontSize = 12.sp,
                        color = TextMediumContrast,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Primary Transport Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButton(
                    onClick = onToggleShuffle,
                    modifier = Modifier.size(44.dp).testTag("shuffle_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffleEnabled) PeachPrimary else TextMediumContrast,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Previous
                IconButton(
                    onClick = onPreviousClick,
                    modifier = Modifier.size(48.dp).testTag("previous_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = TextHighContrast,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Center Play/Pause 72dp FAB
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(PeachPrimary)
                        .clickable(onClick = onPlayPauseClick)
                        .shadow(12.dp, CircleShape, spotColor = PeachPrimary)
                        .testTag("play_pause_fab"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = OnPeachPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Next
                IconButton(
                    onClick = onNextClick,
                    modifier = Modifier.size(48.dp).testTag("next_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = TextHighContrast,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Repeat
                IconButton(
                    onClick = onToggleRepeat,
                    modifier = Modifier.size(44.dp).testTag("repeat_btn")
                ) {
                    Icon(
                        imageVector = if (repeatMode == 2) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = if (repeatMode > 0) PeachPrimary else TextMediumContrast,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Serene Bottom Device & Queue Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Audio Output Pill
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(Color.White.copy(alpha = 0.07f))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = PeachPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pixel Buds Pro",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextHighContrast
                        )
                    }
                }

                // Queue Button Pill
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(Color.White.copy(alpha = 0.07f))
                        .clickable(onClick = onOpenQueue)
                        .padding(horizontal = 12.dp)
                        .testTag("open_queue_pill"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                            contentDescription = null,
                            tint = SageSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Queue",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextHighContrast
                        )
                    }
                }
            }
        }
    }
}

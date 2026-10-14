package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Song
import com.example.ui.theme.OnPeachPrimary
import com.example.ui.theme.PeachPrimary
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SurfaceDarkContainer
import com.example.ui.theme.TextHighContrast
import com.example.ui.theme.TextMediumContrast

@Composable
fun MiniPlayer(
    currentSong: Song?,
    isPlaying: Boolean,
    playbackPositionMs: Long,
    durationMs: Long,
    isFavorite: Boolean,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onPlayerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentSong == null) return

    val progress = if (durationMs > 0) {
        (playbackPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val dotAlpha by pulseTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .shadow(16.dp, RoundedCornerShape(22.dp), spotColor = Color.Black)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF151518))
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(22.dp))
            .clickable(onClick = onPlayerClick)
            .testTag("mini_player")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album Art
                AlbumArtView(
                    artUri = currentSong.albumArtUri,
                    songTitle = currentSong.title,
                    modifier = Modifier.size(42.dp),
                    cornerRadius = 12.dp,
                    iconSize = 20.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Title & Artist
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentSong.title,
                            color = TextHighContrast,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (isPlaying) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(PeachPrimary)
                                    .alpha(dotAlpha)
                            )
                        }
                    }
                    Text(
                        text = currentSong.artist,
                        color = TextMediumContrast,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Actions: Favorite, Play/Pause, Next
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("mini_favorite_btn")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                        tint = if (isFavorite) PeachPrimary else TextMediumContrast,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PeachPrimary)
                        .clickable(onClick = onPlayPauseClick)
                        .testTag("mini_play_pause_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = OnPeachPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = onNextClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("mini_next_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Next song",
                        tint = TextHighContrast,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Micro progress line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(2.5.dp)
                        .background(PeachPrimary)
                )
            }
        }
    }
}

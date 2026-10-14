package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Song
import com.example.ui.theme.OnPeachPrimary
import com.example.ui.theme.PeachPrimary
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.TextHighContrast
import com.example.ui.theme.TextMediumContrast

@Composable
fun FavoritesScreen(
    favoriteSongs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    onSongClick: (Song) -> Unit,
    onPlayAllShuffled: () -> Unit,
    onToggleFavorite: (Song) -> Unit,
    onPlayNext: (Song) -> Unit,
    onAddToQueue: (Song) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .testTag("favorites_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Favorites (${favoriteSongs.size})",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextHighContrast
            )

            if (favoriteSongs.isNotEmpty()) {
                IconButton(
                    onClick = onPlayAllShuffled,
                    modifier = Modifier.testTag("favorites_shuffle_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle favorites",
                        tint = PeachPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        if (favoriteSongs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = PeachPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No favorite songs yet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextHighContrast
                    )
                    Text(
                        text = "Tap the heart icon next to any song to add it here",
                        fontSize = 13.sp,
                        color = TextMediumContrast,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                items(favoriteSongs, key = { it.id }) { song ->
                    val isCurrent = currentSong?.id == song.id
                    SongRowItem(
                        song = song,
                        isCurrent = isCurrent,
                        isPlaying = isPlaying && isCurrent,
                        onClick = { onSongClick(song) },
                        onToggleFavorite = { onToggleFavorite(song) },
                        onPlayNext = { onPlayNext(song) },
                        onAddToQueue = { onAddToQueue(song) }
                    )
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

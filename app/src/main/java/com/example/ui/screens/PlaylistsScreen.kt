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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PlaylistEntity
import com.example.data.model.Song
import com.example.ui.theme.OnPeachPrimary
import com.example.ui.theme.PeachPrimary
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SurfaceDarkContainer
import com.example.ui.theme.TextHighContrast
import com.example.ui.theme.TextMediumContrast

@Composable
fun PlaylistsScreen(
    playlists: List<PlaylistEntity>,
    allSongs: List<Song>,
    onCreatePlaylist: (String) -> Unit,
    onDeletePlaylist: (Long) -> Unit,
    onSongClick: (Song) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .testTag("playlists_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Playlists",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHighContrast
                )
                IconButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PeachPrimary)
                        .testTag("create_playlist_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New playlist",
                        tint = OnPeachPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.QueueMusic,
                            contentDescription = null,
                            tint = PeachPrimary,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No custom playlists yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextHighContrast
                        )
                        Text(
                            text = "Create mixes that survive library filters",
                            fontSize = 13.sp,
                            color = TextMediumContrast,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )
                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PeachPrimary, contentColor = OnPeachPrimary)
                        ) {
                            Text("Create playlist")
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                    items(playlists, key = { it.id }) { playlist ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceDarkContainer)
                                .clickable {
                                    if (allSongs.isNotEmpty()) onSongClick(allSongs.first())
                                }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF242429)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QueueMusic,
                                        contentDescription = null,
                                        tint = PeachPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = playlist.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextHighContrast
                                    )
                                    Text(
                                        text = "Custom playlist",
                                        fontSize = 12.sp,
                                        color = TextMediumContrast,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onDeletePlaylist(playlist.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete playlist",
                                    tint = TextMediumContrast,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }

        // Create Playlist Dialog
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                containerColor = Color(0xFF161619),
                title = {
                    Text("New Playlist", color = TextHighContrast, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text("Enter a name for your playlist:", color = TextMediumContrast, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = newPlaylistName,
                            onValueChange = { newPlaylistName = it },
                            placeholder = { Text("e.g. Midnight Beats") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("playlist_name_input")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newPlaylistName.isNotBlank()) {
                                onCreatePlaylist(newPlaylistName)
                                newPlaylistName = ""
                                showCreateDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PeachPrimary, contentColor = OnPeachPrimary)
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel", color = TextMediumContrast)
                    }
                }
            )
        }
    }
}

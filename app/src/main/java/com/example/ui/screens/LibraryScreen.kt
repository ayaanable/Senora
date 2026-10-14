package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Folder
import com.example.data.model.Song
import com.example.data.settings.SortOption
import com.example.ui.components.AlbumArtView
import com.example.ui.components.EqualizerBars
import com.example.ui.theme.OnPeachPrimary
import com.example.ui.theme.PeachPrimary
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SageSecondary
import com.example.ui.theme.SurfaceDarkContainer
import com.example.ui.theme.TextHighContrast
import com.example.ui.theme.TextMediumContrast
import com.example.ui.viewmodel.LibraryTab

@Composable
fun LibraryScreen(
    songs: List<Song>,
    rawSongCount: Int,
    albums: List<Album>,
    artists: List<Artist>,
    folders: List<Folder>,
    isLoading: Boolean,
    currentSong: Song?,
    isPlaying: Boolean,
    minDurationSeconds: Int,
    selectedTab: LibraryTab,
    sortOption: SortOption,
    sortAscending: Boolean,
    onTabSelected: (LibraryTab) -> Unit,
    onSongClick: (Song) -> Unit,
    onPlayAllShuffled: () -> Unit,
    onToggleFavorite: (Song) -> Unit,
    onOpenFilterSheet: () -> Unit,
    onResetFilter: () -> Unit,
    onSortOptionChange: (SortOption) -> Unit,
    onToggleSortOrder: () -> Unit,
    onSearchClick: () -> Unit,
    onPlayNext: (Song) -> Unit,
    onAddToQueue: (Song) -> Unit,
    onRescanLibrary: () -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .testTag("library_screen")
    ) {
        // 1. Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Library",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextHighContrast
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.testTag("library_search_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextMediumContrast,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(
                    onClick = onOpenFilterSheet,
                    modifier = Modifier.testTag("library_filter_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "Filter",
                        tint = PeachPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // 2. Horizontal Category Pill Tabs Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LibraryTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                val label = when (tab) {
                    LibraryTab.SONGS -> "Songs (${songs.size})"
                    LibraryTab.ALBUMS -> "Albums (${albums.size})"
                    LibraryTab.ARTISTS -> "Artists (${artists.size})"
                    LibraryTab.FOLDERS -> "Folders (${folders.size})"
                    else -> tab.title
                }

                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) PeachPrimary else SurfaceDarkContainer)
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 16.dp)
                        .testTag("tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSelected && tab == LibraryTab.SONGS) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = OnPeachPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) OnPeachPrimary else TextMediumContrast
                        )
                    }
                }
            }
        }

        // 3. Subheader: Active Filter Chip & Sort Bar (when on Songs tab)
        if (selectedTab == LibraryTab.SONGS) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "All Songs",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextHighContrast
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${songs.size}",
                        fontSize = 12.sp,
                        color = TextMediumContrast
                    )

                    // Active Duration Filter Indicator Chip
                    if (minDurationSeconds > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .height(26.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(Color(0xFF1F1F23))
                                .clickable(onClick = onOpenFilterSheet)
                                .padding(horizontal = 8.dp)
                                .testTag("active_filter_chip"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FilterAlt,
                                    contentDescription = null,
                                    tint = PeachPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = ">${minDurationSeconds}s",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextHighContrast
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear duration filter",
                                    tint = TextMediumContrast,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { onResetFilter() }
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showSortMenu = true }
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sortOption.displayName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMediumContrast
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = if (sortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = "Sort order",
                                tint = TextMediumContrast,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            modifier = Modifier.background(Color(0xFF1F1F23))
                        ) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.displayName, color = TextHighContrast) },
                                    onClick = {
                                        showSortMenu = false
                                        if (sortOption == option) {
                                            onToggleSortOrder()
                                        } else {
                                            onSortOptionChange(option)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onPlayAllShuffled,
                        modifier = Modifier.size(32.dp).testTag("library_shuffle_all_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle all songs",
                            tint = TextMediumContrast,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 4. Content Area
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PeachPrimary)
            }
        } else if (songs.isEmpty() && selectedTab == LibraryTab.SONGS) {
            // Distinguish genuinely empty vs filtered out!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = null,
                        tint = PeachPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (rawSongCount > 0) "All tracks hidden by duration filter" else "No music found on device",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextHighContrast
                    )
                    Text(
                        text = if (rawSongCount > 0)
                            "Current filter excludes tracks shorter than ${minDurationSeconds}s"
                        else
                            "Add audio files to storage and tap rescan",
                        fontSize = 13.sp,
                        color = TextMediumContrast,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )
                    if (rawSongCount > 0) {
                        OutlinedButton(onClick = onResetFilter) {
                            Text("Reset filter to 0s", color = PeachPrimary)
                        }
                    } else {
                        OutlinedButton(onClick = onRescanLibrary) {
                            Text("Rescan storage", color = PeachPrimary)
                        }
                    }
                }
            }
        } else {
            when (selectedTab) {
                LibraryTab.SONGS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(songs, key = { it.id }) { song ->
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
                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
                LibraryTab.ALBUMS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(albums) { album ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        val albumTracks = songs.filter { it.album == album.title }
                                        if (albumTracks.isNotEmpty()) onSongClick(albumTracks.first())
                                    }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AlbumArtView(
                                    artUri = album.albumArtUri,
                                    songTitle = album.title,
                                    modifier = Modifier.size(54.dp),
                                    cornerRadius = 14.dp
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = album.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextHighContrast,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${album.artist} • ${album.trackCount} tracks",
                                        fontSize = 13.sp,
                                        color = TextMediumContrast,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
                LibraryTab.ARTISTS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(artists) { artist ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        val artistTracks = songs.filter { it.artist == artist.name }
                                        if (artistTracks.isNotEmpty()) onSongClick(artistTracks.first())
                                    }
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF242429)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = artist.name.take(1).uppercase(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SageSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = artist.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextHighContrast
                                    )
                                    Text(
                                        text = "${artist.trackCount} tracks • ${artist.albumCount} albums",
                                        fontSize = 13.sp,
                                        color = TextMediumContrast
                                    )
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
                LibraryTab.FOLDERS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(folders) { folder ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        val folderTracks = songs.filter { it.album == folder.name }
                                        if (folderTracks.isNotEmpty()) onSongClick(folderTracks.first())
                                    }
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF1E1E22)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = PeachPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = folder.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextHighContrast
                                    )
                                    Text(
                                        text = "${folder.trackCount} tracks • ${folder.path}",
                                        fontSize = 12.sp,
                                        color = TextMediumContrast,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
                else -> {
                    // Fallback to songs
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(songs, key = { it.id }) { song ->
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
    }
}

@Composable
fun SongRowItem(
    song: Song,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail with animated equalizer overlay if playing!
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                AlbumArtView(
                    artUri = song.albumArtUri,
                    songTitle = song.title,
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = 12.dp,
                    iconSize = 22.dp
                )

                if (isCurrent && isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        EqualizerBars()
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    fontSize = 15.sp,
                    fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isCurrent) PeachPrimary else TextHighContrast,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    fontSize = 13.sp,
                    color = TextMediumContrast,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Trailing Controls: Heart, Duration, 3-dots
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (song.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (song.isFavorite) PeachPrimary else TextMediumContrast.copy(alpha = 0.6f),
                    modifier = Modifier.size(19.dp)
                )
            }

            Text(
                text = song.formattedDuration,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMediumContrast,
                modifier = Modifier.width(42.dp),
                maxLines = 1
            )

            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Song options",
                        tint = TextMediumContrast.copy(alpha = 0.7f),
                        modifier = Modifier.size(19.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color(0xFF1F1F23))
                ) {
                    DropdownMenuItem(
                        text = { Text("Play next", color = TextHighContrast) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                                contentDescription = null,
                                tint = SageSecondary
                            )
                        },
                        onClick = {
                            showMenu = false
                            onPlayNext()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add to queue", color = TextHighContrast) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.PlaylistAdd,
                                contentDescription = null,
                                tint = PeachPrimary
                            )
                        },
                        onClick = {
                            showMenu = false
                            onAddToQueue()
                        }
                    )
                }
            }
        }
    }
}

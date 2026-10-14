package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.playback.SonoraMediaPlaybackService
import com.example.ui.components.MiniPlayer
import com.example.ui.components.PlaybackFilterSheet
import com.example.ui.components.QueueSheet
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.NowPlayingScreen
import com.example.ui.screens.PlaylistsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.OnPeachPrimary
import com.example.ui.theme.PeachPrimary
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SageSecondary
import com.example.ui.theme.SageSecondaryContainer
import com.example.ui.theme.SonoraTheme
import com.example.ui.theme.SurfaceDarkContainer
import com.example.ui.theme.SurfaceDarkLow
import com.example.ui.theme.TextHighContrast
import com.example.ui.theme.TextMediumContrast
import com.example.ui.viewmodel.LibraryTab
import com.example.ui.viewmodel.SonoraViewModel

enum class MainNavTab(val label: String) {
    LIBRARY("Library"),
    PLAYLISTS("Playlists"),
    FAVORITES("Favorites"),
    SETTINGS("Settings")
}

class MainActivity : ComponentActivity() {

    private val viewModel: SonoraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Start playback background service
        val serviceIntent = Intent(this, SonoraMediaPlaybackService::class.java)
        try {
            startService(serviceIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            val settings by viewModel.settings.collectAsState()

            val isDark = when (settings.themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            SonoraTheme(
                darkTheme = isDark,
                dynamicColor = settings.dynamicColor,
                amoledDark = true
            ) {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: SonoraViewModel) {
    val context = LocalContext.current

    // Navigation and Modal States
    var currentNavTab by remember { mutableStateOf(MainNavTab.LIBRARY) }
    var isNowPlayingExpanded by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }

    // Permissions check
    val requiredAudioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, requiredAudioPermission) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showPermissionRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (isGranted) {
            viewModel.loadLibrary()
        } else {
            showPermissionRationale = true
        }
    }

    LaunchedEffect(Unit) {
        if (!hasAudioPermission) {
            permissionLauncher.launch(requiredAudioPermission)
        }
    }

    // State collections
    val filteredSongs by viewModel.filteredSongs.collectAsState()
    val rawSongs by viewModel.rawSongs.collectAsState()
    val albums by viewModel.albums.collectAsState()
    val artists by viewModel.artists.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val favoriteSongs by viewModel.favoriteSongs.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val searchedSongs by viewModel.searchedSongs.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val settings by viewModel.settings.collectAsState()

    // Playback state
    val playbackController = viewModel.playbackController
    val currentSong by playbackController.currentSong.collectAsState()
    val isPlaying by playbackController.isPlaying.collectAsState()
    val playbackPositionMs by playbackController.playbackPositionMs.collectAsState()
    val durationMs by playbackController.durationMs.collectAsState()
    val isShuffleEnabled by playbackController.isShuffleEnabled.collectAsState()
    val repeatMode by playbackController.repeatMode.collectAsState()
    val queue by playbackController.queue.collectAsState()
    val queueIndex by playbackController.queueIndex.collectAsState()

    // Sleep Timer state
    val sleepTimerSec by playbackController.sleepTimerManager.remainingSeconds.collectAsState()
    val sleepTimerText = playbackController.sleepTimerManager.formattedRemainingTime

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack),
        bottomBar = {
            if (!isNowPlayingExpanded && !isSearchActive) {
                Column(modifier = Modifier.background(PitchBlack)) {
                    // Persistent Mini Player
                    if (currentSong != null) {
                        MiniPlayer(
                            currentSong = currentSong,
                            isPlaying = isPlaying,
                            playbackPositionMs = playbackPositionMs,
                            durationMs = durationMs,
                            isFavorite = currentSong?.isFavorite ?: false,
                            onPlayPauseClick = { playbackController.togglePlayPause() },
                            onNextClick = { playbackController.skipToNext() },
                            onFavoriteClick = { currentSong?.let { viewModel.toggleFavorite(it) } },
                            onPlayerClick = { isNowPlayingExpanded = true }
                        )
                    }

                    // Pure AMOLED Pixel Material You Bottom Navigation Bar
                    NavigationBar(
                        containerColor = PitchBlack,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .height(68.dp)
                            .testTag("main_navigation_bar")
                    ) {
                        MainNavTab.entries.forEach { tab ->
                            val isSelected = currentNavTab == tab
                            val icon = when (tab) {
                                MainNavTab.LIBRARY -> Icons.Default.LibraryMusic
                                MainNavTab.PLAYLISTS -> Icons.Default.QueueMusic
                                MainNavTab.FAVORITES -> Icons.Default.Favorite
                                MainNavTab.SETTINGS -> Icons.Default.Settings
                            }

                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentNavTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = tab.label,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = OnPeachPrimary,
                                    selectedTextColor = TextHighContrast,
                                    indicatorColor = PeachPrimary,
                                    unselectedIconColor = TextMediumContrast,
                                    unselectedTextColor = TextMediumContrast
                                ),
                                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .background(PitchBlack)
        ) {
            // Main Tab Screens
            when (currentNavTab) {
                MainNavTab.LIBRARY -> {
                    LibraryScreen(
                        songs = filteredSongs,
                        rawSongCount = rawSongs.size,
                        albums = albums,
                        artists = artists,
                        folders = folders,
                        isLoading = isLoading,
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        minDurationSeconds = settings.minDurationSeconds,
                        selectedTab = selectedTab,
                        sortOption = settings.sortOption,
                        sortAscending = settings.sortAscending,
                        onTabSelected = { viewModel.setSelectedTab(it) },
                        onSongClick = { song -> viewModel.playSong(song, filteredSongs) },
                        onPlayAllShuffled = {
                            if (filteredSongs.isNotEmpty()) {
                                playbackController.toggleShuffle()
                                viewModel.playSong(filteredSongs.random(), filteredSongs)
                            }
                        },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onOpenFilterSheet = { showFilterSheet = true },
                        onResetFilter = { viewModel.setMinimumDuration(0) },
                        onSortOptionChange = { viewModel.setSortOption(it, settings.sortAscending) },
                        onToggleSortOrder = { viewModel.toggleSortOrder() },
                        onSearchClick = { isSearchActive = true },
                        onPlayNext = { playbackController.playNext(it) },
                        onAddToQueue = { playbackController.addToQueue(it) },
                        onRescanLibrary = { viewModel.loadLibrary() }
                    )
                }

                MainNavTab.PLAYLISTS -> {
                    PlaylistsScreen(
                        playlists = playlists,
                        allSongs = filteredSongs,
                        onCreatePlaylist = { viewModel.createPlaylist(it) },
                        onDeletePlaylist = { viewModel.deletePlaylist(it) },
                        onSongClick = { song -> viewModel.playSong(song) }
                    )
                }

                MainNavTab.FAVORITES -> {
                    FavoritesScreen(
                        favoriteSongs = favoriteSongs,
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        onSongClick = { song -> viewModel.playSong(song, favoriteSongs) },
                        onPlayAllShuffled = {
                            if (favoriteSongs.isNotEmpty()) {
                                playbackController.toggleShuffle()
                                viewModel.playSong(favoriteSongs.random(), favoriteSongs)
                            }
                        },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onPlayNext = { playbackController.playNext(it) },
                        onAddToQueue = { playbackController.addToQueue(it) }
                    )
                }

                MainNavTab.SETTINGS -> {
                    SettingsScreen(
                        settings = settings,
                        songCount = filteredSongs.size,
                        rawSongCount = rawSongs.size,
                        onMinDurationClick = { showFilterSheet = true },
                        onSleepTimerClick = { showFilterSheet = true },
                        onDynamicColorToggle = { viewModel.setDynamicColor(it) },
                        onArtworkAccentsToggle = { viewModel.setArtworkAccents(it) },
                        onGaplessToggle = { viewModel.setGaplessPlayback(it) },
                        onThemeSelect = { viewModel.setThemeMode(it) },
                        onRescanClick = { viewModel.loadLibrary() },
                        onBackClick = { currentNavTab = MainNavTab.LIBRARY }
                    )
                }
            }

            // Search Screen Overlay
            AnimatedVisibility(
                visible = isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SearchScreen(
                    searchQuery = searchQuery,
                    searchResults = searchedSongs,
                    currentSong = currentSong,
                    isPlaying = isPlaying,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onSongClick = { song -> viewModel.playSong(song, searchedSongs) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onPlayNext = { playbackController.playNext(it) },
                    onAddToQueue = { playbackController.addToQueue(it) },
                    onBackClick = {
                        viewModel.setSearchQuery("")
                        isSearchActive = false
                    }
                )
            }

            // Fullscreen Now Playing Overlay with smooth slide-up
            AnimatedVisibility(
                visible = isNowPlayingExpanded,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NowPlayingScreen(
                    currentSong = currentSong,
                    isPlaying = isPlaying,
                    playbackPositionMs = playbackPositionMs,
                    durationMs = durationMs,
                    isShuffleEnabled = isShuffleEnabled,
                    repeatMode = repeatMode,
                    isFavorite = currentSong?.isFavorite ?: false,
                    sleepTimerText = sleepTimerText,
                    onCollapse = { isNowPlayingExpanded = false },
                    onPlayPauseClick = { playbackController.togglePlayPause() },
                    onNextClick = { playbackController.skipToNext() },
                    onPreviousClick = { playbackController.skipToPrevious() },
                    onSeekTo = { playbackController.seekTo(it) },
                    onToggleShuffle = { playbackController.toggleShuffle() },
                    onToggleRepeat = { playbackController.toggleRepeat() },
                    onToggleFavorite = { currentSong?.let { viewModel.toggleFavorite(it) } },
                    onOpenQueue = { showQueueSheet = true },
                    onOpenSleepTimer = { showFilterSheet = true }
                )
            }

            // Modal Bottom Sheet: Playback & Library (Minimum Duration & Sleep Timer)
            if (showFilterSheet) {
                PlaybackFilterSheet(
                    minDurationSeconds = settings.minDurationSeconds,
                    sleepTimerRemainingSeconds = sleepTimerSec,
                    sleepTimerFadeOut = settings.sleepTimerFadeOut,
                    onMinDurationChange = { viewModel.setMinimumDuration(it) },
                    onStartSleepTimer = { viewModel.startSleepTimer(it) },
                    onAddFiveMinutes = { viewModel.addSleepTimerMinutes(5) },
                    onCancelSleepTimer = { viewModel.cancelSleepTimer() },
                    onFadeOutToggle = { viewModel.setSleepTimerFadeOut(it) },
                    onDismissRequest = { showFilterSheet = false }
                )
            }

            // Modal Bottom Sheet: Queue
            if (showQueueSheet) {
                QueueSheet(
                    queue = queue,
                    currentIndex = queueIndex,
                    isPlaying = isPlaying,
                    onSongClick = { song -> viewModel.playSong(song, queue) },
                    onRemoveItem = { playbackController.removeFromQueue(it) },
                    onClearQueue = { playbackController.clearQueue() },
                    onDismissRequest = { showQueueSheet = false }
                )
            }

            // Permission Rationale Dialog
            if (showPermissionRationale) {
                AlertDialog(
                    onDismissRequest = { showPermissionRationale = false },
                    containerColor = Color(0xFF161619),
                    title = {
                        Text(
                            text = "Music Storage Access",
                            color = TextHighContrast,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = "Sonora is a 100% offline local music player. To play music stored on your device, Sonora needs permission to read audio files from your storage. Your personal data never leaves your device.",
                            color = TextMediumContrast,
                            fontSize = 14.sp
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showPermissionRationale = false
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PeachPrimary, contentColor = OnPeachPrimary)
                        ) {
                            Text("Open App Settings")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showPermissionRationale = false }) {
                            Text("Dismiss", color = TextMediumContrast)
                        }
                    }
                )
            }
        }
    }
}

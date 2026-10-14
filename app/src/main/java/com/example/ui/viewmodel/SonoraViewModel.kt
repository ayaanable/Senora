package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.SonoraApplication
import com.example.data.filter.LibraryFilter
import com.example.data.local.FavoriteEntity
import com.example.data.local.PlaylistEntity
import com.example.data.local.PlaylistItemEntity
import com.example.data.local.RecentlyPlayedEntity
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Folder
import com.example.data.model.Song
import com.example.data.settings.SonoraSettings
import com.example.data.settings.SortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LibraryTab(val title: String) {
    SONGS("Songs"),
    ALBUMS("Albums"),
    ARTISTS("Artists"),
    PLAYLISTS("Playlists"),
    FOLDERS("Folders"),
    FAVORITES("Favorites"),
    RECENTLY_PLAYED("Recently Played")
}

class SonoraViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as SonoraApplication
    private val mediaRepo = app.mediaStoreRepository
    private val settingsRepo = app.settingsRepository
    private val dao = app.database.sonoraDao()
    val playbackController = app.playbackController

    // Raw songs discovered
    private val _rawSongs = MutableStateFlow<List<Song>>(emptyList())
    val rawSongs: StateFlow<List<Song>> = _rawSongs.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedTab = MutableStateFlow(LibraryTab.SONGS)
    val selectedTab: StateFlow<LibraryTab> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val settings: StateFlow<SonoraSettings> = settingsRepo.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, SonoraSettings())

    val favoriteIds: StateFlow<List<Long>> = dao.getAllFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playlists: StateFlow<List<PlaylistEntity>> = dao.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyPlayedIds: StateFlow<List<Long>> = dao.getRecentlyPlayedSongIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered and sorted songs
    val filteredSongs: StateFlow<List<Song>> = combine(
        _rawSongs,
        settings,
        favoriteIds
    ) { songs, currentSettings, favIds ->
        val withFavs = songs.map { song ->
            song.copy(isFavorite = favIds.contains(song.id))
        }
        val durationFiltered = LibraryFilter.filterByDuration(withFavs, currentSettings.minDurationSeconds)
        LibraryFilter.sortSongs(durationFiltered, currentSettings.sortOption, currentSettings.sortAscending)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Search results (respects duration filter)
    val searchedSongs: StateFlow<List<Song>> = combine(
        filteredSongs,
        _searchQuery
    ) { songs, query ->
        LibraryFilter.searchSongs(songs, query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Derived Albums, Artists, Folders from filtered songs
    val albums: StateFlow<List<Album>> = filteredSongs.combine(_rawSongs) { filtered, _ ->
        mediaRepo.extractAlbums(filtered)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val artists: StateFlow<List<Artist>> = filteredSongs.combine(_rawSongs) { filtered, _ ->
        mediaRepo.extractArtists(filtered)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val folders: StateFlow<List<Folder>> = filteredSongs.combine(_rawSongs) { filtered, _ ->
        mediaRepo.extractFolders(filtered)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteSongs: StateFlow<List<Song>> = combine(
        _rawSongs,
        favoriteIds
    ) { songs, favIds ->
        songs.filter { favIds.contains(it.id) }.map { it.copy(isFavorite = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyPlayedSongs: StateFlow<List<Song>> = combine(
        _rawSongs,
        recentlyPlayedIds
    ) { songs, recentIds ->
        val songMap = songs.associateBy { it.id }
        recentIds.mapNotNull { songMap[it] }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadLibrary()
    }

    fun loadLibrary() {
        viewModelScope.launch {
            _isLoading.value = true
            val loaded = mediaRepo.loadSongs()
            _rawSongs.value = loaded
            _isLoading.value = false

            // If nothing is playing, load first song into player queue
            if (playbackController.currentSong.value == null && loaded.isNotEmpty()) {
                val eligible = LibraryFilter.filterByDuration(loaded, settings.value.minDurationSeconds)
                val first = eligible.firstOrNull() ?: loaded.first()
                playbackController.playSong(first, eligible)
                playbackController.pause() // keep paused initially
            }
        }
    }

    fun setSelectedTab(tab: LibraryTab) {
        _selectedTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun playSong(song: Song, queueContext: List<Song>? = null) {
        val actualQueue = queueContext ?: filteredSongs.value
        playbackController.playSong(song, actualQueue)
        // Record recently played
        viewModelScope.launch {
            dao.recordRecentlyPlayed(RecentlyPlayedEntity(song.id))
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            val isFav = favoriteIds.value.contains(song.id)
            if (isFav) {
                dao.removeFavorite(song.id)
            } else {
                dao.addFavorite(FavoriteEntity(song.id, song.title, song.artist))
            }
        }
    }

    fun setMinimumDuration(seconds: Int) {
        viewModelScope.launch {
            settingsRepo.setMinDurationSeconds(seconds)
        }
    }

    fun setSortOption(sortOption: SortOption, ascending: Boolean) {
        viewModelScope.launch {
            settingsRepo.setSortOption(sortOption, ascending)
        }
    }

    fun toggleSortOrder() {
        viewModelScope.launch {
            val current = settings.value
            settingsRepo.setSortOption(current.sortOption, !current.sortAscending)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            settingsRepo.setThemeMode(mode)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setDynamicColor(enabled)
        }
    }

    fun setArtworkAccents(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setArtworkAccents(enabled)
        }
    }

    fun setGaplessPlayback(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setGaplessPlayback(enabled)
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                dao.insertPlaylist(PlaylistEntity(name = name.trim()))
            }
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            dao.deletePlaylist(playlistId)
            dao.deletePlaylistItems(playlistId)
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            dao.insertPlaylistItem(PlaylistItemEntity(playlistId = playlistId, songId = songId))
        }
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            dao.removePlaylistItem(playlistId, songId)
        }
    }

    // Sleep Timer actions
    fun startSleepTimer(minutes: Int) {
        playbackController.sleepTimerManager.startTimer(minutes)
    }

    fun addSleepTimerMinutes(minutes: Int) {
        playbackController.sleepTimerManager.addMinutes(minutes)
    }

    fun cancelSleepTimer() {
        playbackController.sleepTimerManager.cancelTimer()
    }

    fun setSleepTimerFadeOut(enabled: Boolean) {
        playbackController.sleepTimerManager.fadeOutEnabled = enabled
        viewModelScope.launch {
            settingsRepo.setSleepTimerFadeOut(enabled)
        }
    }
}

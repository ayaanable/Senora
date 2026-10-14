package com.example.playback

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.model.Song
import com.example.domain.SleepTimerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class SonoraPlaybackController(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var progressJob: Job? = null

    val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true // handle audio focus
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                addListener(playerListener)
            }
    }

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackPositionMs = MutableStateFlow(0L)
    val playbackPositionMs: StateFlow<Long> = _playbackPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    // 0 = Off, 1 = Repeat All, 2 = Repeat One
    private val _repeatMode = MutableStateFlow(0)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue.asStateFlow()

    private val _queueIndex = MutableStateFlow(0)
    val queueIndex: StateFlow<Int> = _queueIndex.asStateFlow()

    val sleepTimerManager = SleepTimerManager(
        context = context,
        onFadeVolume = { volume ->
            exoPlayer.volume = volume.coerceIn(0f, 1f)
        },
        onTimerExpired = {
            pause()
        }
    )

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            if (isPlaying) {
                startProgressTracker()
            } else {
                stopProgressTracker()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                val dur = exoPlayer.duration
                if (dur > 0) {
                    _durationMs.value = dur
                }
            } else if (playbackState == Player.STATE_ENDED) {
                onTrackEnded()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            error.printStackTrace()
            // On playback error, skip to next track or notify
            skipToNext()
        }
    }

    fun playSong(song: Song, newQueue: List<Song> = emptyList()) {
        val updatedQueue = if (newQueue.isNotEmpty()) newQueue else if (_queue.value.isEmpty()) listOf(song) else _queue.value
        val index = updatedQueue.indexOfFirst { it.id == song.id }.let { if (it >= 0) it else 0 }

        _queue.value = updatedQueue
        _queueIndex.value = index
        _currentSong.value = song
        _durationMs.value = song.durationMs

        loadAndPlay(song)
    }

    private fun loadAndPlay(song: Song) {
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist)
            .setAlbumTitle(song.album)
            .apply {
                song.albumArtUri?.let { setArtworkUri(Uri.parse(it)) }
            }
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(song.contentUri)
            .setMediaMetadata(mediaMetadata)
            .build()

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
        _playbackPositionMs.value = 0L
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            if (exoPlayer.playbackState == Player.STATE_IDLE || exoPlayer.mediaItemCount == 0) {
                _currentSong.value?.let { loadAndPlay(it) }
            } else {
                exoPlayer.play()
            }
        }
    }

    fun play() {
        if (!exoPlayer.isPlaying) {
            exoPlayer.play()
        }
    }

    fun pause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        }
    }

    fun seekTo(positionMs: Long) {
        val safePos = positionMs.coerceIn(0L, _durationMs.value.coerceAtLeast(0L))
        _playbackPositionMs.value = safePos
        exoPlayer.seekTo(safePos)
    }

    fun skipToNext() {
        val currentQueue = _queue.value
        if (currentQueue.isEmpty()) return

        val nextIndex = if (_isShuffleEnabled.value) {
            currentQueue.indices.random()
        } else {
            (_queueIndex.value + 1) % currentQueue.size
        }

        _queueIndex.value = nextIndex
        val nextSong = currentQueue[nextIndex]
        _currentSong.value = nextSong
        _durationMs.value = nextSong.durationMs
        loadAndPlay(nextSong)
    }

    fun skipToPrevious() {
        // If playback is more than 3 seconds in, seek to beginning
        if (exoPlayer.currentPosition > 3000) {
            seekTo(0)
            return
        }

        val currentQueue = _queue.value
        if (currentQueue.isEmpty()) return

        val prevIndex = if (_queueIndex.value - 1 < 0) currentQueue.size - 1 else _queueIndex.value - 1
        _queueIndex.value = prevIndex
        val prevSong = currentQueue[prevIndex]
        _currentSong.value = prevSong
        _durationMs.value = prevSong.durationMs
        loadAndPlay(prevSong)
    }

    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
    }

    fun toggleRepeat() {
        // Cycle: 0 (Off) -> 1 (All) -> 2 (One) -> 0 (Off)
        val nextMode = (_repeatMode.value + 1) % 3
        _repeatMode.value = nextMode
        exoPlayer.repeatMode = when (nextMode) {
            1 -> Player.REPEAT_MODE_ALL
            2 -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
    }

    fun addToQueue(song: Song) {
        val current = _queue.value.toMutableList()
        current.add(song)
        _queue.value = current
    }

    fun playNext(song: Song) {
        val current = _queue.value.toMutableList()
        val insertIndex = (_queueIndex.value + 1).coerceAtMost(current.size)
        current.add(insertIndex, song)
        _queue.value = current
    }

    fun removeFromQueue(index: Int) {
        val current = _queue.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _queue.value = current
            if (index < _queueIndex.value) {
                _queueIndex.value = (_queueIndex.value - 1).coerceAtLeast(0)
            }
        }
    }

    fun moveQueueItem(fromIndex: Int, toIndex: Int) {
        val current = _queue.value.toMutableList()
        if (fromIndex in current.indices && toIndex in current.indices) {
            val item = current.removeAt(fromIndex)
            current.add(toIndex, item)
            _queue.value = current
            if (_queueIndex.value == fromIndex) {
                _queueIndex.value = toIndex
            }
        }
    }

    fun clearQueue() {
        _queue.value = emptyList()
        _queueIndex.value = 0
    }

    private fun onTrackEnded() {
        when (_repeatMode.value) {
            2 -> {
                // Repeat One
                seekTo(0)
                exoPlayer.play()
            }
            1 -> {
                // Repeat All
                skipToNext()
            }
            else -> {
                // Repeat Off
                if (_queueIndex.value < _queue.value.size - 1) {
                    skipToNext()
                } else {
                    pause()
                    seekTo(0)
                }
            }
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                _playbackPositionMs.value = exoPlayer.currentPosition
                val dur = exoPlayer.duration
                if (dur > 0) _durationMs.value = dur
                delay(250)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        _playbackPositionMs.value = exoPlayer.currentPosition
    }

    fun release() {
        progressJob?.cancel()
        sleepTimerManager.cancelTimer()
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }
}

package com.example.data.model

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long = 0L,
    val durationMs: Long,
    val contentUri: String,
    val albumArtUri: String? = null,
    val dateAdded: Long = System.currentTimeMillis() / 1000,
    val trackNumber: Int = 1,
    val sizeBytes: Long = 0L,
    val mimeType: String = "audio/mpeg",
    val audioQualityBadge: String = "Hi-Res",
    val sampleRateText: String = "44.1 kHz",
    val isFavorite: Boolean = false,
    val isBuiltInShowcase: Boolean = false
) {
    val durationSeconds: Long
        get() = durationMs / 1000

    val formattedDuration: String
        get() {
            val totalSeconds = (durationMs / 1000).coerceAtLeast(0)
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val trackCount: Int,
    val albumArtUri: String? = null,
    val year: Int? = null
)

data class Artist(
    val name: String,
    val trackCount: Int,
    val albumCount: Int
)

data class Folder(
    val name: String,
    val path: String,
    val trackCount: Int
)

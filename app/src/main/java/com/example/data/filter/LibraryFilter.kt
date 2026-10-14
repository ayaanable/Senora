package com.example.data.filter

import com.example.data.model.Song
import com.example.data.settings.SortOption

object LibraryFilter {

    /**
     * Filters songs based on the minimum duration threshold in seconds.
     * Boundary is inclusive: a track with duration == minDurationSeconds is eligible.
     * When minDurationSeconds is 0 ("All tracks" / "Off"), short tracks are allowed.
     * When minDurationSeconds > 0, tracks with unknown/0 duration are excluded.
     */
    fun filterByDuration(songs: List<Song>, minDurationSeconds: Int): List<Song> {
        if (minDurationSeconds <= 0) {
            return songs
        }
        return songs.filter { song ->
            if (song.durationMs <= 0) {
                false
            } else {
                song.durationSeconds >= minDurationSeconds
            }
        }
    }

    /**
     * Sorts songs according to the chosen SortOption and ascending/descending order.
     */
    fun sortSongs(songs: List<Song>, sortOption: SortOption, ascending: Boolean): List<Song> {
        val sorted = when (sortOption) {
            SortOption.TITLE -> songs.sortedBy { it.title.lowercase() }
            SortOption.ARTIST -> songs.sortedBy { it.artist.lowercase() }
            SortOption.ALBUM -> songs.sortedBy { it.album.lowercase() }
            SortOption.DURATION -> songs.sortedBy { it.durationMs }
            SortOption.DATE_ADDED -> songs.sortedBy { it.dateAdded }
        }
        return if (ascending) sorted else sorted.reversed()
    }

    /**
     * Searches songs across title, artist, album, and content filename.
     */
    fun searchSongs(songs: List<Song>, query: String): List<Song> {
        if (query.isBlank()) return songs
        val trimmed = query.trim().lowercase()
        return songs.filter { song ->
            song.title.lowercase().contains(trimmed) ||
            song.artist.lowercase().contains(trimmed) ||
            song.album.lowercase().contains(trimmed) ||
            song.contentUri.lowercase().contains(trimmed)
        }
    }
}

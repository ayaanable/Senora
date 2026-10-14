package com.example

import com.example.data.filter.LibraryFilter
import com.example.data.model.Song
import com.example.data.settings.SortOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SonoraBusinessLogicTest {

    private val testSongs = listOf(
        Song(
            id = 1L,
            title = "Alpha Voice Memo",
            artist = "Speaker A",
            album = "Memos",
            durationMs = 5_000L, // 5s
            contentUri = "content://audio/1"
        ),
        Song(
            id = 2L,
            title = "Beta Short Jingle",
            artist = "Sound FX",
            album = "Jingles",
            durationMs = 15_000L, // 15s
            contentUri = "content://audio/2"
        ),
        Song(
            id = 3L,
            title = "Gamma Exact Thirty",
            artist = "Kavinsky & Tycho",
            album = "Horizons",
            durationMs = 30_000L, // 30s
            contentUri = "content://audio/3"
        ),
        Song(
            id = 4L,
            title = "Delta Solaris Drift",
            artist = "Tycho",
            album = "Simulcast",
            durationMs = 90_000L, // 90s
            contentUri = "content://audio/4"
        ),
        Song(
            id = 5L,
            title = "Epsilon Unknown Duration",
            artist = "Unknown",
            album = "Unknown",
            durationMs = 0L, // Unknown / 0ms
            contentUri = "content://audio/5"
        )
    )

    @Test
    fun durationFilter_zeroThreshold_includesAllPlayableTracks() {
        val result = LibraryFilter.filterByDuration(testSongs, 0)
        assertEquals(5, result.size)
    }

    @Test
    fun durationFilter_thirtySeconds_boundaryIsInclusive() {
        // Acceptance test from prompt:
        // Given tracks of 5, 15, 30, and 90 seconds, setting minimum duration to 30s
        // must display the 30-second and 90-second tracks.
        val result = LibraryFilter.filterByDuration(testSongs, 30)

        // Must include 30s (Gamma) and 90s (Delta), and exclude 5s (Alpha), 15s (Beta), and 0s (Epsilon)
        assertEquals(2, result.size)
        assertTrue(result.any { it.title == "Gamma Exact Thirty" })
        assertTrue(result.any { it.title == "Delta Solaris Drift" })
        assertFalse(result.any { it.title == "Alpha Voice Memo" })
        assertFalse(result.any { it.title == "Beta Short Jingle" })
        assertFalse(result.any { it.title == "Epsilon Unknown Duration" })
    }

    @Test
    fun durationFilter_tenSeconds_excludesFiveAndZero() {
        val result = LibraryFilter.filterByDuration(testSongs, 10)
        assertEquals(3, result.size)
        assertFalse(result.any { it.durationMs < 10_000L })
    }

    @Test
    fun durationFilter_doesNotMutateSourceList() {
        val originalCount = testSongs.size
        val result = LibraryFilter.filterByDuration(testSongs, 60)
        assertEquals(1, result.size)
        assertEquals(originalCount, testSongs.size)
    }

    @Test
    fun searchCombinedWithDurationFilter() {
        // First filter by duration >= 30s
        val durationFiltered = LibraryFilter.filterByDuration(testSongs, 30)
        // Search for "Tycho"
        val searched = LibraryFilter.searchSongs(durationFiltered, "tycho")

        assertEquals(2, searched.size)
        assertTrue(searched.any { it.title == "Gamma Exact Thirty" })
        assertTrue(searched.any { it.title == "Delta Solaris Drift" })

        // Searching for "Voice" which is only 5s should return empty because it was filtered out
        val voiceSearch = LibraryFilter.searchSongs(durationFiltered, "voice")
        assertTrue(voiceSearch.isEmpty())
    }

    @Test
    fun sorting_byTitle_ascendingAndDescending() {
        val asc = LibraryFilter.sortSongs(testSongs, SortOption.TITLE, ascending = true)
        assertEquals("Alpha Voice Memo", asc.first().title)
        assertEquals("Gamma Exact Thirty", asc.last().title)

        val desc = LibraryFilter.sortSongs(testSongs, SortOption.TITLE, ascending = false)
        assertEquals("Gamma Exact Thirty", desc.first().title)
        assertEquals("Alpha Voice Memo", desc.last().title)
    }

    @Test
    fun sorting_byDuration() {
        val asc = LibraryFilter.sortSongs(testSongs, SortOption.DURATION, ascending = true)
        assertEquals(0L, asc.first().durationMs)
        assertEquals(90_000L, asc.last().durationMs)
    }

    @Test
    fun songFormattedDuration_formatsCorrectly() {
        val song4Min12Sec = Song(
            id = 99L,
            title = "Test Track",
            artist = "Artist",
            album = "Album",
            durationMs = 252_000L,
            contentUri = "content://audio/99"
        )
        assertEquals("4:12", song4Min12Sec.formattedDuration)

        val song5Sec = Song(
            id = 98L,
            title = "Short Alert",
            artist = "System",
            album = "Sounds",
            durationMs = 5_000L,
            contentUri = "content://audio/98"
        )
        assertEquals("0:05", song5Sec.formattedDuration)
    }
}

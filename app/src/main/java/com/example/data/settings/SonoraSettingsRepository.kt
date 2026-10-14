package com.example.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sonora_settings")

enum class SortOption(val displayName: String) {
    DATE_ADDED("Date Added"),
    TITLE("Title"),
    ARTIST("Artist"),
    ALBUM("Album"),
    DURATION("Duration")
}

data class SonoraSettings(
    val minDurationSeconds: Int = 30,
    val themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val dynamicColor: Boolean = true,
    val artworkAccents: Boolean = true,
    val gaplessPlayback: Boolean = true,
    val defaultSleepTimerMinutes: Int = 30,
    val sleepTimerFadeOut: Boolean = true,
    val sortOption: SortOption = SortOption.DATE_ADDED,
    val sortAscending: Boolean = false,
    val defaultLibraryTab: String = "SONGS"
)

class SonoraSettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val MIN_DURATION_SECONDS = intPreferencesKey("min_duration_seconds")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val ARTWORK_ACCENTS = booleanPreferencesKey("artwork_accents")
        val GAPLESS_PLAYBACK = booleanPreferencesKey("gapless_playback")
        val DEFAULT_SLEEP_TIMER_MINUTES = intPreferencesKey("default_sleep_timer_minutes")
        val SLEEP_TIMER_FADE_OUT = booleanPreferencesKey("sleep_timer_fade_out")
        val SORT_OPTION = stringPreferencesKey("sort_option")
        val SORT_ASCENDING = booleanPreferencesKey("sort_ascending")
        val DEFAULT_LIBRARY_TAB = stringPreferencesKey("default_library_tab")
    }

    val settingsFlow: Flow<SonoraSettings> = context.dataStore.data.map { preferences ->
        val sortString = preferences[PreferencesKeys.SORT_OPTION] ?: SortOption.DATE_ADDED.name
        val sortOption = try {
            SortOption.valueOf(sortString)
        } catch (e: Exception) {
            SortOption.DATE_ADDED
        }

        SonoraSettings(
            minDurationSeconds = preferences[PreferencesKeys.MIN_DURATION_SECONDS] ?: 30,
            themeMode = preferences[PreferencesKeys.THEME_MODE] ?: "SYSTEM",
            dynamicColor = preferences[PreferencesKeys.DYNAMIC_COLOR] ?: true,
            artworkAccents = preferences[PreferencesKeys.ARTWORK_ACCENTS] ?: true,
            gaplessPlayback = preferences[PreferencesKeys.GAPLESS_PLAYBACK] ?: true,
            defaultSleepTimerMinutes = preferences[PreferencesKeys.DEFAULT_SLEEP_TIMER_MINUTES] ?: 30,
            sleepTimerFadeOut = preferences[PreferencesKeys.SLEEP_TIMER_FADE_OUT] ?: true,
            sortOption = sortOption,
            sortAscending = preferences[PreferencesKeys.SORT_ASCENDING] ?: false,
            defaultLibraryTab = preferences[PreferencesKeys.DEFAULT_LIBRARY_TAB] ?: "SONGS"
        )
    }

    suspend fun setMinDurationSeconds(seconds: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MIN_DURATION_SECONDS] = seconds.coerceIn(0, 600)
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun setArtworkAccents(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ARTWORK_ACCENTS] = enabled
        }
    }

    suspend fun setGaplessPlayback(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GAPLESS_PLAYBACK] = enabled
        }
    }

    suspend fun setDefaultSleepTimerMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_SLEEP_TIMER_MINUTES] = minutes.coerceIn(1, 180)
        }
    }

    suspend fun setSleepTimerFadeOut(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SLEEP_TIMER_FADE_OUT] = enabled
        }
    }

    suspend fun setSortOption(sortOption: SortOption, ascending: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_OPTION] = sortOption.name
            preferences[PreferencesKeys.SORT_ASCENDING] = ascending
        }
    }
}

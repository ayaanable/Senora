# Sonora — A Pixel-Inspired Native Android Music Player

Sonora is a native, offline-first music player crafted for Android 14 and 15, embodying the refined visual language, tactile motion design, and system synergy of Google Pixel software.

---

## Highlights

- **Pure Pixel Experience**: Designed with Material You dynamic color, pitch-black AMOLED surfaces (`#000000`), generous spacing, and custom squircle and pill geometries.
- **Media3 ExoPlayer & MediaSessionService**: Robust background audio playback, system media notification controls, audio focus management, and "audio becoming noisy" (headphone disconnect) handling.
- **Minimum Song Duration Filter**: Inclusive library threshold filter (0s–600s with presets: Off, 10s, 30s, 60s, 2m). Hides voice memos and notification sound effects while preserving original files and custom playlists.
- **Sleep Timer & Gentle Fade-Out**: Wall-clock deadline persistence, +5m quick addition, presets (15m, 30m, 45m, 60m), and an optional perceptual volume fade-out during the final 30 seconds.
- **Jetpack Glance Home-Screen Widgets**:
  - **2×2 Compact Widget**: Album cover, track metadata, quick play/pause FAB, and skip next.
  - **4×2 Expanded Widget**: Large album artwork, FLAC/Hi-Res quality badges, progress scrubber, and transport controls.
- **Local Database & Offline Privacy**: Built on Android MediaStore, Room, and DataStore. Zero telemetry, zero analytics, zero network tracking, and 100% offline playback.

---

## Architecture & Code Structure

The project strictly adheres to MVVM, unidirectional data flow (UDF), and clean modular separation:

```
app/src/main/java/com/example/
├── SonoraApplication.kt           // Application entry point holding shared singletons
├── MainActivity.kt                // Edge-to-edge Compose host & navigation coordinator
├── data/
│   ├── model/
│   │   └── Song.kt               // Song, Album, Artist, Folder domain models
│   ├── local/
│   │   ├── Entities.kt           // Room entities for Favorites, Playlists, History
│   │   ├── SonoraDao.kt          // SQLite Room DAO queries & flows
│   │   └── SonoraDatabase.kt     // Local Room Database singleton
│   ├── settings/
│   │   └── SonoraSettingsRepository.kt // Jetpack DataStore Preferences repository
│   ├── repository/
│   │   └── MediaStoreRepository.kt     // MediaStore audio queries & showcase library
│   └── filter/
│       └── LibraryFilter.kt      // Inclusive duration filtering, search & sorting
├── domain/
│   └── SleepTimerManager.kt      // Monotonic deadline sleep timer with volume fade-out
├── playback/
│   ├── SonoraPlaybackController.kt   // ExoPlayer controller, queue, shuffle & repeat
│   ├── SonoraMediaPlaybackService.kt // MediaSessionService foreground audio service
│   └── AudioToneGenerator.kt     // Playable PCM audio waveform synthesizer
├── widget/
│   └── SonoraWidget.kt           // Jetpack Glance 2×2 and 4×2 home-screen widgets
└── ui/
    ├── theme/
    │   ├── Color.kt              // Obsidian Acoustic & Pixel Material You palette
    │   ├── Theme.kt              // Dynamic color schemes and AMOLED surfaces
    │   └── Type.kt               // System typography hierarchy
    ├── components/
    │   ├── MiniPlayer.kt         // Floating AMOLED mini-player pill
    │   ├── PlaybackFilterSheet.kt// Modal bottom sheet for duration filter & timer
    │   ├── QueueSheet.kt         // Playback queue management sheet
    │   ├── AlbumArtView.kt       // Artwork loader with Pixel-style fallback
    │   └── EqualizerBars.kt      // Animated 3-bar playing equalizer
    ├── viewmodel/
    │   └── SonoraViewModel.kt    // Unified state flow coordinator
    └── screens/
        ├── LibraryScreen.kt      // Songs, albums, artists, folders, filter pill
        ├── NowPlayingScreen.kt   // Centerpiece player with ambient glowing aura
        ├── SettingsScreen.kt     // Preferences & Glance widget showcase
        ├── PlaylistsScreen.kt    // Custom user playlist creator
        ├── FavoritesScreen.kt    // Starred tracks collection
        └── SearchScreen.kt       // Instant debounced library search
```

---

## Minimum Song Duration Filter

The minimum song duration filter is implemented in `com.example.data.filter.LibraryFilter` and persisted in `SonoraSettingsRepository`.

### Inclusive Boundary Behavior
- Given tracks of 5s, 15s, 30s, and 90s:
  - Setting the filter to **30s** includes the 30-second and 90-second tracks, while filtering out the 5s and 15s tracks.
  - Setting the filter to **Off (0s)** shows all playable audio files.
  - Songs with unknown or 0ms duration are filtered out when the threshold is > 0s, but included when the threshold is 0s.
- Changing the slider reactively updates the Songs screen, Albums, Artists, Folders, and Search results.
- Custom user playlists retain all songs, preventing silent track deletion.

---

## Sleep Timer & Gentle Fade-Out

- Managed by `SleepTimerManager`.
- Preserves a monotonic deadline based on `SystemClock.elapsedRealtime()`.
- Calculates remaining seconds reactively every 500ms.
- **Gentle Fade-Out**: If enabled, during the last 30 seconds of the timer, ExoPlayer's volume is smoothly reduced to 0 along a perceptual volume curve, without modifying the Android system's master volume.
- Gracefully pauses playback on expiry and resets volume scaling for subsequent sessions.

---

## Home-Screen Widget (Glance)

- Declared in `SonoraWidget.kt` and `AndroidManifest.xml` via `SonoraWidgetReceiver`.
- Responsive layout adapting to 2×2 (compact pill) and 4×2 (expanded audio card).
- Interacts directly with `SonoraPlaybackController` through Glance `ActionCallback` handlers, allowing play/pause, next, and previous actions from the launcher.

---

## Permissions & Privacy

- **Android 13+ (API 33+)**: `READ_MEDIA_AUDIO`
- **Android 12 and below**: `READ_EXTERNAL_STORAGE` (maxSdkVersion=32)
- **Foreground Service**: `FOREGROUND_SERVICE` and `FOREGROUND_SERVICE_MEDIA_PLAYBACK`
- **Zero Internet Requirement**: Sonora does not connect to external servers or send telemetry. All index data, favorites, and playlists reside on-device.

---

## Build & Test Instructions

### Compilation
```bash
gradle :app:assembleDebug
```

### Running Unit Tests
```bash
gradle :app:testDebugUnitTest
```
Tests in `SonoraBusinessLogicTest.kt` verify inclusive duration filtering, search combinations, sorting permutations, and duration formatting.

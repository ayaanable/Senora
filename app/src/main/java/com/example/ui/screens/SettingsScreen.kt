package com.example.ui.screens

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.settings.SonoraSettings
import com.example.ui.components.AlbumArtView
import com.example.ui.theme.OnPeachPrimary
import com.example.ui.theme.PeachPrimary
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SageSecondary
import com.example.ui.theme.SageSecondaryContainer
import com.example.ui.theme.SurfaceDarkContainer
import com.example.ui.theme.SurfaceDarkHigh
import com.example.ui.theme.SurfaceDarkLow
import com.example.ui.theme.TextHighContrast
import com.example.ui.theme.TextMediumContrast
import com.example.widget.SonoraWidgetReceiver
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settings: SonoraSettings,
    songCount: Int,
    rawSongCount: Int,
    onMinDurationClick: () -> Unit,
    onSleepTimerClick: () -> Unit,
    onDynamicColorToggle: (Boolean) -> Unit,
    onArtworkAccentsToggle: (Boolean) -> Unit,
    onGaplessToggle: (Boolean) -> Unit,
    onThemeSelect: (String) -> Unit,
    onRescanClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var isRescanning by remember { mutableStateOf(false) }
    val spinRotation = remember { Animatable(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .testTag("settings_screen")
    ) {
        // 1. Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Settings",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextHighContrast
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search settings",
                        tint = TextMediumContrast
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PeachPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = OnPeachPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 2. Interactive Breadcrumb Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SurfaceDarkLow)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextHighContrast,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Player & System Preferences",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMediumContrast
                )
            }

            // Glance V2.4 Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2C4E36).copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(SageSecondary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GLANCE V2.4",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SageSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Live Android Widget Showcase Canvas
        SectionHeader(
            title = "Home-Screen Widgets (Glance)",
            icon = Icons.Default.Widgets,
            iconTint = PeachPrimary,
            subtitle = "Native Pixel widgets with Material You dynamic color"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF0E0E11))
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(28.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Launcher Mock Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Pixel Launcher Live Mock",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextHighContrast
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Android 15 QPR2",
                            fontSize = 11.sp,
                            color = TextMediumContrast
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PeachPrimary))
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF353438)))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Widget Preview 1: 2x2 Glance Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF131316))
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
                            .padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                AlbumArtView(
                                    artUri = null,
                                    songTitle = "Solaris Drift",
                                    modifier = Modifier.size(44.dp),
                                    cornerRadius = 14.dp
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceDarkHigh),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = PeachPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Solaris Drift",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextHighContrast,
                                    maxLines = 1
                                )
                                Text(
                                    text = "Tycho",
                                    fontSize = 11.sp,
                                    color = TextMediumContrast,
                                    maxLines = 1
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(PeachPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Pause,
                                        contentDescription = null,
                                        tint = OnPeachPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceDarkHigh),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SkipNext,
                                        contentDescription = null,
                                        tint = TextHighContrast,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.06f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "2×2",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SageSecondary
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "2×2 Glance Pill",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PeachPrimary
                        )
                        Text(
                            text = "Designed for tight launcher spaces with quick pause & next taps.",
                            fontSize = 12.sp,
                            color = TextMediumContrast,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = SageSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Dynamic cover tinting",
                                fontSize = 11.sp,
                                color = TextMediumContrast
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Widget Preview 2: 4x2 Full Glance
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(Color(0xFF131316))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(26.dp))
                        .padding(14.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AlbumArtView(
                                artUri = null,
                                songTitle = "Epoch",
                                modifier = Modifier.size(56.dp),
                                cornerRadius = 16.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Epoch (Dive Remix)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextHighContrast,
                                        maxLines = 1
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFE7C268),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "Tycho • Simulcast Deluxe",
                                    fontSize = 12.sp,
                                    color = TextMediumContrast,
                                    maxLines = 1
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(SurfaceDarkHigh)
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text("FLAC 24-bit", fontSize = 10.sp, color = TextMediumContrast)
                                    }
                                    Text("48.0 kHz", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = SageSecondary)
                                }
                            }
                        }

                        // Progress Scrubber Line
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("02:18", fontSize = 11.sp, color = TextMediumContrast)
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(1.5.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(1.5.dp))
                                        .background(PeachPrimary)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("03:45", fontSize = 11.sp, color = TextMediumContrast)
                        }

                        // Transport Controls
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    tint = SageSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("4×2 Full Glance", fontSize = 11.sp, color = TextMediumContrast)
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceDarkHigh),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SkipPrevious,
                                        contentDescription = null,
                                        tint = TextHighContrast,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(PeachPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Pause,
                                        contentDescription = null,
                                        tint = OnPeachPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceDarkHigh),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SkipNext,
                                        contentDescription = null,
                                        tint = TextHighContrast,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // "Add Widget to Home screen" Button
                Button(
                    onClick = {
                        val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                        val provider = ComponentName(context, SonoraWidgetReceiver::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
                            appWidgetManager.requestPinAppWidget(provider, null, null)
                            Toast.makeText(context, "Pinning Sonora widget to Home screen", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Long-press your Home screen to add Sonora Glance widget", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("add_widget_btn"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SageSecondaryContainer,
                        contentColor = SageSecondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCard,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Widget to Home screen",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. GROUP: Appearance
        SettingsCard(
            title = "Appearance",
            subtitle = "Pixel dynamic themes & color palettes",
            icon = Icons.Default.Palette,
            iconTint = PeachPrimary
        ) {
            SettingsRow(
                title = "App Theme",
                subtitle = "System default (${settings.themeMode.lowercase().replaceFirstChar { it.uppercase() }})",
                onClick = {
                    val nextTheme = when (settings.themeMode) {
                        "DARK" -> "LIGHT"
                        "LIGHT" -> "SYSTEM"
                        else -> "DARK"
                    }
                    onThemeSelect(nextTheme)
                }
            )

            SettingsSwitchRow(
                title = "Dynamic Color",
                subtitle = "Material You wallpaper tonal tinting",
                checked = settings.dynamicColor,
                onCheckedChange = onDynamicColorToggle
            )

            SettingsSwitchRow(
                title = "Artwork Accents",
                subtitle = "Derive player & scrubber shades from active cover art",
                checked = settings.artworkAccents,
                onCheckedChange = onArtworkAccentsToggle
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. GROUP: Audio & Playback
        SettingsCard(
            title = "Audio & Playback",
            subtitle = "DSP, gapless engine, and device routing",
            icon = Icons.Default.Tune,
            iconTint = SageSecondary
        ) {
            val hiddenCount = (rawSongCount - songCount).coerceAtLeast(0)
            val durationSubtitle = if (settings.minDurationSeconds > 0) {
                "${settings.minDurationSeconds}s ($songCount tracks visible • $hiddenCount hidden)"
            } else {
                "Off (All $songCount tracks visible)"
            }

            SettingsRow(
                title = "Minimum song duration",
                subtitle = durationSubtitle,
                onClick = onMinDurationClick
            )

            SettingsRow(
                title = "Sleep timer default",
                subtitle = "${settings.defaultSleepTimerMinutes} mins (${if (settings.sleepTimerFadeOut) "30s gentle fade-out" else "instant pause"})",
                onClick = onSleepTimerClick
            )

            SettingsSwitchRow(
                title = "Gapless playback",
                subtitle = "Sample-accurate zero-latency track transitions",
                checked = settings.gaplessPlayback,
                onCheckedChange = onGaplessToggle
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Audio Output Target", fontSize = 14.sp, color = TextHighContrast)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = SageSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Pixel Buds Pro (LDAC Lossless)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SageSecondary
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDarkHigh)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("990 kbps", fontSize = 11.sp, color = TextMediumContrast)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6. GROUP: Library & Storage
        SettingsCard(
            title = "Library & Storage",
            subtitle = "Local index and offline sandboxing",
            icon = Icons.Default.FolderSpecial,
            iconTint = Color(0xFFE7C268)
        ) {
            // Rescan Local Storage
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        if (!isRescanning) {
                            isRescanning = true
                            scope.launch {
                                spinRotation.animateTo(
                                    targetValue = spinRotation.value + 360f,
                                    animationSpec = tween(durationMillis = 800, easing = LinearEasing)
                                )
                                onRescanClick()
                                isRescanning = false
                                Toast.makeText(context, "Library rescan complete: $rawSongCount tracks", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Rescan local storage", fontSize = 14.sp, color = TextHighContrast)
                    Text(
                        text = if (isRescanning) "Scanning storage..." else "Last scanned just now • $rawSongCount tracks",
                        fontSize = 12.sp,
                        color = TextMediumContrast,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SurfaceDarkHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Rescan",
                        tint = TextHighContrast,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(spinRotation.value)
                    )
                }
            }

            // Storage Permission
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Storage permission", fontSize = 14.sp, color = TextHighContrast)
                    Text(
                        text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            "READ_MEDIA_AUDIO • Granted"
                        else
                            "READ_EXTERNAL_STORAGE • Granted",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SageSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = SageSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Privacy & Safety
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Privacy & Safety", fontSize = 14.sp, color = TextHighContrast)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = SageSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "100% Offline • Zero telemetry, analytics, or trackers",
                        fontSize = 12.sp,
                        color = TextMediumContrast,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SageSecondaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Strict",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SageSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 7. GROUP: About Sonora
        SettingsCard(
            title = "About Sonora",
            subtitle = "Pure local audio for Android",
            icon = Icons.Default.Info,
            iconTint = PeachPrimary
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Sonora Pixel Edition", fontSize = 14.sp, color = TextHighContrast)
                    Text("Version 1.0.0 (Build 4128)", fontSize = 12.sp, color = TextMediumContrast)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PeachPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Up to date", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = PeachPrimary)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        Toast.makeText(context, "Apache License 2.0 • AndroidX Media3 ExoPlayer", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Open Source Licenses", fontSize = 14.sp, color = TextHighContrast)
                    Text("Apache 2.0, ExoPlayer v2, Glance", fontSize = 12.sp, color = TextMediumContrast)
                }
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    tint = TextMediumContrast,
                    modifier = Modifier.size(18.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        Toast.makeText(context, "github.com/sonoralabs/sonora-audio-player", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("GitHub Repository", fontSize = 14.sp, color = TextHighContrast)
                    Text("source / sonoralabs / sonora-audio-player", fontSize = 12.sp, color = TextMediumContrast)
                }
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = TextMediumContrast,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun SectionHeader(title: String, icon: ImageVector, iconTint: Color, subtitle: String) {
    Column(modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextHighContrast
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = TextMediumContrast,
            modifier = Modifier.padding(top = 1.dp)
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(SurfaceDarkLow)
            .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(28.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SurfaceDarkHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextHighContrast)
                    Text(subtitle, fontSize = 12.sp, color = TextMediumContrast)
                }
            }

            content()
        }
    }
}

@Composable
private fun SettingsRow(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, color = TextHighContrast)
            Text(subtitle, fontSize = 12.sp, color = TextMediumContrast, modifier = Modifier.padding(top = 2.dp))
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMediumContrast,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, color = TextHighContrast)
            Text(subtitle, fontSize = 12.sp, color = TextMediumContrast, modifier = Modifier.padding(top = 2.dp))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = OnPeachPrimary,
                checkedTrackColor = PeachPrimary,
                uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
            )
        )
    }
}

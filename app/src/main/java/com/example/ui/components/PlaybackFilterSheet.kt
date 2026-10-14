package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OnPeachPrimary
import com.example.ui.theme.PeachPrimary
import com.example.ui.theme.PeachPrimaryContainer
import com.example.ui.theme.SurfaceDarkContainer
import com.example.ui.theme.SurfaceDarkLow
import com.example.ui.theme.TextHighContrast
import com.example.ui.theme.TextMediumContrast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackFilterSheet(
    minDurationSeconds: Int,
    sleepTimerRemainingSeconds: Int,
    sleepTimerFadeOut: Boolean,
    onMinDurationChange: (Int) -> Unit,
    onStartSleepTimer: (Int) -> Unit,
    onAddFiveMinutes: () -> Unit,
    onCancelSleepTimer: () -> Unit,
    onFadeOutToggle: (Boolean) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val presetDurations = listOf(
        0 to "Off",
        10 to "10s",
        30 to "30s",
        60 to "60s",
        120 to "2m"
    )

    val sleepPresets = listOf(15, 30, 45, 60)

    var currentDurationSlider by remember(minDurationSeconds) {
        val stepIndex = presetDurations.indexOfFirst { it.first == minDurationSeconds }
        mutableFloatStateOf(if (stepIndex >= 0) stepIndex.toFloat() else 2f)
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color(0xFF0C0C0E),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            )
        },
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Playback & Library",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextHighContrast
                )
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextMediumContrast,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // SECTION 1: Minimum Song Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Minimum Duration",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextHighContrast
                    )
                    Text(
                        text = "Hides short voice clips and sound effects",
                        fontSize = 13.sp,
                        color = TextMediumContrast,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.Bottom) {
                    val displayVal = when (minDurationSeconds) {
                        0 -> "0"
                        10 -> "10"
                        30 -> "30"
                        60 -> "60"
                        120 -> "2"
                        else -> minDurationSeconds.toString()
                    }
                    val displayUnit = if (minDurationSeconds == 120) "MIN" else "SEC"

                    Text(
                        text = displayVal,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = PeachPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = displayUnit,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMediumContrast,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Smooth Slider
            Slider(
                value = currentDurationSlider,
                onValueChange = { newVal ->
                    currentDurationSlider = newVal
                    val idx = newVal.toInt().coerceIn(0, presetDurations.size - 1)
                    onMinDurationChange(presetDurations[idx].first)
                },
                valueRange = 0f..4f,
                steps = 3,
                colors = SliderDefaults.colors(
                    thumbColor = PeachPrimary,
                    activeTrackColor = PeachPrimary,
                    inactiveTrackColor = Color.White.copy(alpha = 0.12f)
                ),
                modifier = Modifier.fillMaxWidth().testTag("duration_slider")
            )

            // Preset Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presetDurations.forEachIndexed { index, (secs, label) ->
                    val isSelected = minDurationSeconds == secs
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isSelected) PeachPrimary else Color.White.copy(alpha = 0.05f))
                            .clickable {
                                currentDurationSlider = index.toFloat()
                                onMinDurationChange(secs)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) OnPeachPrimary else TextMediumContrast
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 2: Sleep Timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Sleep Timer",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextHighContrast
                    )
                    val statusText = if (sleepTimerRemainingSeconds > 0) {
                        val m = sleepTimerRemainingSeconds / 60
                        val s = sleepTimerRemainingSeconds % 60
                        if (m > 0) "${m}m remaining" else "${s}s remaining"
                    } else {
                        "Off"
                    }
                    Text(
                        text = statusText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (sleepTimerRemainingSeconds > 0) PeachPrimary else TextMediumContrast,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (sleepTimerRemainingSeconds > 0) {
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .clickable(onClick = onCancelSleepTimer)
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextHighContrast
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .height(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable(onClick = onAddFiveMinutes)
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = TextHighContrast,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "5m",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextHighContrast
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Preset timer pills: 15m, 30m, 45m, 60m
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sleepPresets.forEach { mins ->
                    val isSelected = sleepTimerRemainingSeconds in (mins * 60 - 60)..(mins * 60 + 5)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isSelected) PeachPrimary else Color.White.copy(alpha = 0.05f))
                            .clickable { onStartSleepTimer(mins) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${mins}m",
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) OnPeachPrimary else TextMediumContrast
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Gentle fade-out switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gentle fade-out",
                    fontSize = 14.sp,
                    color = TextHighContrast
                )
                Switch(
                    checked = sleepTimerFadeOut,
                    onCheckedChange = onFadeOutToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = OnPeachPrimary,
                        checkedTrackColor = PeachPrimary,
                        uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                        uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Apply Button
            Button(
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("apply_filter_btn"),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PeachPrimary,
                    contentColor = OnPeachPrimary
                )
            ) {
                Text(
                    text = "Apply",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

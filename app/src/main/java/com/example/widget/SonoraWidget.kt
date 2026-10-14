package com.example.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.MainActivity
import com.example.R
import com.example.SonoraApplication

class SonoraWidget : GlanceAppWidget() {

    companion object {
        private val SMALL_BOX = DpSize(120.dp, 100.dp)
        private val EXPANDED_BOX = DpSize(240.dp, 100.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(SMALL_BOX, EXPANDED_BOX)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        val size = LocalSize.current
        val isExpanded = size.width >= 220.dp

        val app = try { SonoraApplication.instance } catch (e: Exception) { null }
        val currentSong = app?.playbackController?.currentSong?.value
        val isPlaying = app?.playbackController?.isPlaying?.value ?: false

        val title = currentSong?.title ?: "Solaris Drift"
        val artist = currentSong?.artist ?: "Kavinsky & Tycho"
        val album = currentSong?.album ?: "Epoch Horizons"

        if (isExpanded) {
            ExpandedWidget(title, artist, album, isPlaying)
        } else {
            CompactWidget(title, artist, isPlaying)
        }
    }

    @Composable
    private fun CompactWidget(title: String, artist: String, isPlaying: Boolean) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF131316))
                .cornerRadius(24.dp)
                .padding(12.dp)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_sonora_logo),
                        contentDescription = "Cover",
                        modifier = GlanceModifier
                            .size(40.dp)
                            .cornerRadius(12.dp)
                    )
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            text = title,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = artist,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontSize = 11.sp
                            ),
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.End
                ) {
                    Box(
                        modifier = GlanceModifier
                            .size(36.dp)
                            .background(Color(0xFFFFB4A2))
                            .cornerRadius(18.dp)
                            .clickable(actionRunCallback<PlayPauseActionCallback>()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isPlaying) "❚❚" else "▶",
                            style = TextStyle(
                                color = ColorProvider(Color(0xFF512317)),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                    }

                    Spacer(modifier = GlanceModifier.width(8.dp))

                    Box(
                        modifier = GlanceModifier
                            .size(34.dp)
                            .background(Color(0xFF242429))
                            .cornerRadius(17.dp)
                            .clickable(actionRunCallback<NextActionCallback>()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⏭",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ExpandedWidget(title: String, artist: String, album: String, isPlaying: Boolean) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF131316))
                .cornerRadius(28.dp)
                .padding(14.dp)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_sonora_logo),
                        contentDescription = "Cover",
                        modifier = GlanceModifier
                            .size(52.dp)
                            .cornerRadius(16.dp)
                    )
                    Spacer(modifier = GlanceModifier.width(12.dp))
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            text = title,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = "$artist • $album",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontSize = 12.sp
                            ),
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(10.dp))

                // Track Progress Line representation
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0xFF242429))
                        .cornerRadius(2.dp)
                ) {
                    Box(
                        modifier = GlanceModifier
                            .width(120.dp)
                            .height(4.dp)
                            .background(Color(0xFFFFB4A2))
                            .cornerRadius(2.dp)
                    ) {}
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.End
                ) {
                    Box(
                        modifier = GlanceModifier
                            .size(36.dp)
                            .background(Color(0xFF1A1A1E))
                            .cornerRadius(18.dp)
                            .clickable(actionRunCallback<PreviousActionCallback>()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⏮",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontSize = 14.sp
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.width(10.dp))

                    Box(
                        modifier = GlanceModifier
                            .size(42.dp)
                            .background(Color(0xFFFFB4A2))
                            .cornerRadius(21.dp)
                            .clickable(actionRunCallback<PlayPauseActionCallback>()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isPlaying) "❚❚" else "▶",
                            style = TextStyle(
                                color = ColorProvider(Color(0xFF512317)),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                    }

                    Spacer(modifier = GlanceModifier.width(10.dp))

                    Box(
                        modifier = GlanceModifier
                            .size(36.dp)
                            .background(Color(0xFF1A1A1E))
                            .cornerRadius(18.dp)
                            .clickable(actionRunCallback<NextActionCallback>()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⏭",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

class SonoraWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SonoraWidget()
}

class PlayPauseActionCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        try {
            SonoraApplication.instance.playbackController.togglePlayPause()
            SonoraWidget().update(context, glanceId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

class NextActionCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        try {
            SonoraApplication.instance.playbackController.skipToNext()
            SonoraWidget().update(context, glanceId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

class PreviousActionCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        try {
            SonoraApplication.instance.playbackController.skipToPrevious()
            SonoraWidget().update(context, glanceId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

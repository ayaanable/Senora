package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ui.theme.PeachPrimary
import com.example.ui.theme.PeachPrimaryContainer
import com.example.ui.theme.SageSecondary
import com.example.ui.theme.SurfaceDarkHigh

@Composable
fun AlbumArtView(
    artUri: String?,
    songTitle: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    iconSize: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(SurfaceDarkHigh),
        contentAlignment = Alignment.Center
    ) {
        if (!artUri.isNullOrBlank()) {
            AsyncImage(
                model = artUri,
                contentDescription = "Album art for $songTitle",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Elegant Pixel-inspired warm aesthetic gradient fallback
            val hash = songTitle.hashCode()
            val gradientBrush = when (kotlin.math.abs(hash) % 3) {
                0 -> Brush.linearGradient(listOf(Color(0xFF2C1914), Color(0xFF5A2518), Color(0xFF1E100D)))
                1 -> Brush.linearGradient(listOf(Color(0xFF1C2618), Color(0xFF384A2C), Color(0xFF10170D)))
                else -> Brush.linearGradient(listOf(Color(0xFF2E2215), Color(0xFF543E1F), Color(0xFF18120B)))
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradientBrush),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = PeachPrimary.copy(alpha = 0.85f),
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

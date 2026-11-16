package com.snowfort.toss.core.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.snowfort.toss.core.theme.MittenOrange
import com.snowfort.toss.core.theme.SurfaceMuted

@Composable
fun StarRow(
    earned: Int,
    total: Int,
    modifier: Modifier = Modifier,
    starSize: Dp = 40.dp
) {
    var revealed by remember(earned) { mutableStateOf(false) }
    LaunchedEffect(earned) {
        revealed = true
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { index ->
            val active = revealed && index < earned
            val scale by animateFloatAsState(
                targetValue = if (active) 1f else 0.62f,
                animationSpec = if (active) {
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                } else {
                    tween(durationMillis = 120)
                },
                label = "starPop"
            )
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (index < earned) MittenOrange else SurfaceMuted,
                modifier = Modifier
                    .size(starSize)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            )
        }
    }
}

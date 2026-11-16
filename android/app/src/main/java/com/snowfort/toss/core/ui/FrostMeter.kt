package com.snowfort.toss.core.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.snowfort.toss.core.theme.FrostBlue
import com.snowfort.toss.core.theme.SurfaceMuted

@Composable
fun FrostMeter(
    filled: Int,
    segments: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(segments) { index ->
            val active = index < filled
            val glow by animateFloatAsState(
                targetValue = if (active) 1f else 0.4f,
                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                label = "frostSegment"
            )
            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(6.dp)
                    .alpha(glow)
                    .shadow(
                        elevation = if (active) 6.dp else 0.dp,
                        shape = RoundedCornerShape(3.dp),
                        ambientColor = FrostBlue,
                        spotColor = FrostBlue
                    )
                    .background(
                        color = if (active) FrostBlue else SurfaceMuted,
                        shape = RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}

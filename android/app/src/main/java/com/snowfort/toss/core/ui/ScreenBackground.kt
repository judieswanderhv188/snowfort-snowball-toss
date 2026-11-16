package com.snowfort.toss.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.snowfort.toss.R
import com.snowfort.toss.core.theme.TextPrimary
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ScreenBackground(
    overlay: List<Color>,
    modifier: Modifier = Modifier,
    flakes: Int = 18,
    flakeAlpha: Float = 0.18f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(overlay))
        )
        if (flakes > 0) {
            SnowDrift(count = flakes, alpha = flakeAlpha)
        }
        content()
    }
}

@Composable
fun SnowDrift(count: Int, alpha: Float, modifier: Modifier = Modifier) {
    val seeds = remember(count) {
        val random = Random(count * 131L + 17L)
        List(count) {
            FlakeSeed(
                x = random.nextFloat(),
                offset = random.nextFloat(),
                radius = 1.5f + random.nextFloat() * 2.5f,
                drift = 0.01f + random.nextFloat() * 0.04f,
                speed = 0.7f + random.nextFloat() * 0.6f
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "snow")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "snowProgress"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        seeds.forEach { seed ->
            val travel = (progress * seed.speed + seed.offset) % 1f
            val centerX = (seed.x + sin((travel + seed.offset) * 6.2831f) * seed.drift) * size.width
            val centerY = travel * size.height
            drawCircle(
                color = TextPrimary.copy(alpha = alpha),
                radius = seed.radius * density,
                center = Offset(centerX, centerY)
            )
        }
    }
}

private data class FlakeSeed(
    val x: Float,
    val offset: Float,
    val radius: Float,
    val drift: Float,
    val speed: Float
)

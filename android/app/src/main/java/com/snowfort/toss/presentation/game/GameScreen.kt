package com.snowfort.toss.presentation.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snowfort.toss.R
import com.snowfort.toss.core.config.GameConfig
import com.snowfort.toss.core.di.AppViewModelFactory
import com.snowfort.toss.core.theme.FrostBlue
import com.snowfort.toss.core.theme.MittenOrange
import com.snowfort.toss.core.theme.MittenOrangeLight
import com.snowfort.toss.core.theme.NightBase
import com.snowfort.toss.core.theme.SurfaceMuted
import com.snowfort.toss.core.theme.SurfaceSheet
import com.snowfort.toss.core.theme.TextMuted
import com.snowfort.toss.core.theme.TextPrimary
import com.snowfort.toss.core.theme.TextSecondary
import com.snowfort.toss.core.theme.VioletPrimary
import com.snowfort.toss.core.ui.FrostMeter
import com.snowfort.toss.core.ui.GlassSurface
import com.snowfort.toss.core.ui.PrimaryButton
import com.snowfort.toss.core.ui.ScreenBackground
import com.snowfort.toss.presentation.dialog.PauseDialog
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameScreen(
    onExit: () -> Unit,
    onRoundFinished: (Int, Int, Int, Int, Boolean) -> Unit,
    viewModel: GameViewModel = viewModel(factory = AppViewModelFactory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        viewModel.resume()
        onDispose { viewModel.pause() }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { result ->
            onRoundFinished(result.yard, result.hits, result.shots, result.stars, result.cleared)
        }
    }

    GameContent(
        state = state,
        onBack = onExit,
        onPause = { viewModel.setPaused(true) },
        onResume = { viewModel.setPaused(false) },
        onThrow = { viewModel.onThrowClick() },
        onAimStart = { viewModel.onAimStart() },
        onAimDrag = { dx, dy -> viewModel.onAimDrag(dx, dy) },
        onAimRelease = { viewModel.onAimRelease() }
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onBack: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onThrow: () -> Unit,
    onAimStart: () -> Unit,
    onAimDrag: (Float, Float) -> Unit,
    onAimRelease: () -> Unit
) {
    ScreenBackground(
        overlay = listOf(
            NightBase.copy(alpha = 0.70f),
            NightBase.copy(alpha = 0.40f),
            SurfaceSheet.copy(alpha = 0.90f)
        ),
        flakes = 18,
        flakeAlpha = 0.18f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.cd_back),
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.yard_label, state.yard),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    letterSpacing = 2.sp
                )
                IconButton(
                    onClick = onPause,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_pause),
                        contentDescription = stringResource(R.string.cd_pause),
                        tint = TextPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Arena(
                state = state,
                onAimStart = onAimStart,
                onAimDrag = onAimDrag,
                onAimRelease = onAimRelease,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            StatStrip(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.game_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                PrimaryButton(
                    text = if (state.tripleReady) {
                        stringResource(R.string.triple_throw)
                    } else {
                        stringResource(R.string.throw_action)
                    },
                    onClick = onThrow,
                    icon = Icons.Default.PlayArrow,
                    enabled = state.canThrow || state.phase == GamePhase.CHARGING,
                    height = 60.dp,
                    gradient = if (state.tripleReady) {
                        listOf(FrostBlue, VioletPrimary)
                    } else {
                        listOf(MittenOrange, MittenOrangeLight)
                    }
                )
            }
        }

        if (state.paused) {
            PauseDialog(
                onResume = onResume,
                onMenu = onBack
            )
        }
    }
}

@Composable
private fun Arena(
    state: GameUiState,
    onAimStart: () -> Unit,
    onAimDrag: (Float, Float) -> Unit,
    onAimRelease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "arena")
    val wobble by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "targetWobble"
    )
    val bonusPulse by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bonusPulse"
    )

    BoxWithConstraints(
        modifier = modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { onAimStart() },
                onDragEnd = { onAimRelease() },
                onDragCancel = { onAimRelease() },
                onDrag = { change, dragAmount ->
                    change.consume()
                    val width = size.width.toFloat()
                    if (width > 0f) {
                        onAimDrag(dragAmount.x / width, dragAmount.y / width)
                    }
                }
            )
        }
    ) {
        val arenaWidth = maxWidth
        val arenaHeight = maxHeight

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawOval(
                color = SurfaceMuted.copy(alpha = 0.85f),
                topLeft = Offset(-size.width * 0.25f, size.height * 0.62f),
                size = Size(size.width * 0.95f, size.height * 0.75f)
            )
            drawOval(
                color = NightBase.copy(alpha = 0.75f),
                topLeft = Offset(size.width * 0.38f, size.height * 0.68f),
                size = Size(size.width * 0.9f, size.height * 0.7f)
            )
            state.previewDots.forEachIndexed { index, dot ->
                val fade = 0.9f - index * (0.65f / (state.previewDots.size + 1))
                drawCircle(
                    color = FrostBlue.copy(alpha = fade.coerceIn(0.25f, 0.9f)),
                    radius = 3.5.dp.toPx(),
                    center = Offset(dot.x * size.width, dot.y * size.height)
                )
            }
            if (state.aimActive) {
                val ringRadius = 44.dp.toPx()
                val center = Offset(
                    GameConfig.ORIGIN_X * size.width,
                    GameConfig.ORIGIN_Y * size.height
                )
                drawArc(
                    color = MittenOrange,
                    startAngle = -90f,
                    sweepAngle = 360f * state.power,
                    useCenter = false,
                    topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
                    size = Size(ringRadius * 2f, ringRadius * 2f),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
            state.puffs.forEach { puff ->
                val life = puff.ageMs / 340f
                val alpha = (1f - life).coerceIn(0f, 1f) * if (puff.strong) 0.9f else 0.5f
                repeat(8) { index ->
                    val angle = index * 0.7854f
                    val spread = (10f + life * 26f).dp.toPx()
                    drawCircle(
                        color = TextPrimary.copy(alpha = alpha * 0.8f),
                        radius = (2f + (1f - life) * 4f).dp.toPx(),
                        center = Offset(
                            puff.position.x * size.width + cos(angle) * spread,
                            puff.position.y * size.height + sin(angle) * spread
                        )
                    )
                }
            }
        }

        state.targets.forEach { target ->
            key(target.id) {
                val scale by animateFloatAsState(
                    targetValue = when {
                        target.knocked -> 0f
                        target.hidden -> 0.08f
                        else -> 1f
                    },
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                    label = "targetScale"
                )
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = stringResource(R.string.cd_target),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .offset(
                            x = arenaWidth * target.position.x - 36.dp,
                            y = arenaHeight * target.position.y - 36.dp
                        )
                        .size(72.dp)
                        .graphicsLayer {
                            rotationZ = wobble
                            scaleY = scale
                            scaleX = 1f - (1f - scale) * 0.25f
                            transformOrigin = TransformOrigin(0.5f, 1f)
                        }
                )
            }
        }

        if (state.tripleReady) {
            Image(
                painter = painterResource(R.drawable.sprite_5),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .offset(
                        x = arenaWidth * 0.34f,
                        y = arenaHeight * 0.66f
                    )
                    .size(40.dp)
                    .alpha(bonusPulse)
            )
        }

        state.projectiles.forEach { ball ->
            key(ball.id) {
                Image(
                    painter = painterResource(R.drawable.sprite_2),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .offset(
                            x = arenaWidth * ball.position.x - 14.dp,
                            y = arenaHeight * ball.position.y - 14.dp
                        )
                        .size(28.dp)
                        .graphicsLayer { rotationZ = ball.spin }
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.sprite_3),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(120.dp)
        )
    }
}

@Composable
private fun StatStrip(
    state: GameUiState,
    modifier: Modifier = Modifier
) {
    val frostDescription = if (state.tripleReady) {
        stringResource(R.string.triple_ready)
    } else {
        stringResource(R.string.frost_label)
    }
    GlassSurface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        container = SurfaceSheet.copy(alpha = 0.90f),
        borderColor = TextPrimary.copy(alpha = 0.10f),
        elevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCell(
                label = stringResource(R.string.snowballs_label),
                value = state.snowballsLeft.toString(),
                valueColor = TextPrimary
            )
            Box(
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            ) {
                StatCell(
                    label = stringResource(R.string.hits_label),
                    value = stringResource(
                        R.string.hits_value,
                        state.hits,
                        state.targets.size.coerceAtLeast(1)
                    ),
                    valueColor = MittenOrange
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.semantics { stateDescription = frostDescription }
            ) {
                Text(
                    text = frostDescription,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state.tripleReady) FrostBlue else TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                FrostMeter(
                    filled = state.frost,
                    segments = GameConfig.FROST_SEGMENTS
                )
            }
        }
    }
}

@Composable
private fun StatCell(
    label: String,
    value: String,
    valueColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor
        )
    }
}

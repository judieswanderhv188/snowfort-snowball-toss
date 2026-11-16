package com.snowfort.toss.presentation.menu

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snowfort.toss.R
import com.snowfort.toss.core.di.AppViewModelFactory
import com.snowfort.toss.core.theme.FrostBlue
import com.snowfort.toss.core.theme.MittenOrange
import com.snowfort.toss.core.theme.NightBase
import com.snowfort.toss.core.theme.SurfaceMuted
import com.snowfort.toss.core.theme.SurfaceSheet
import com.snowfort.toss.core.theme.TextPrimary
import com.snowfort.toss.core.theme.TextSecondary
import com.snowfort.toss.core.ui.GlassSurface
import com.snowfort.toss.core.ui.PrimaryButton
import com.snowfort.toss.core.ui.ScreenBackground
import com.snowfort.toss.core.ui.SecondaryButton
import com.snowfort.toss.core.ui.StarBadge
import com.snowfort.toss.core.ui.StatCard

@Composable
fun MenuScreen(
    onPlay: () -> Unit,
    onFort: () -> Unit,
    onShop: () -> Unit,
    onSettings: () -> Unit,
    viewModel: MenuViewModel = viewModel(factory = AppViewModelFactory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        viewModel.refresh()
        onDispose { }
    }

    MenuContent(
        state = state,
        onPlay = onPlay,
        onFort = onFort,
        onShop = onShop,
        onSettings = onSettings
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: () -> Unit,
    onFort: () -> Unit,
    onShop: () -> Unit,
    onSettings: () -> Unit
) {
    val progressDescription = stringResource(
        R.string.yard_progress,
        state.currentYard,
        state.totalYards
    )
    val sheetOffset by animateFloatAsState(
        targetValue = if (state.loaded) 0f else 40f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "sheetOffset"
    )
    val sheetAlpha by animateFloatAsState(
        targetValue = if (state.loaded) 1f else 0f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "sheetAlpha"
    )

    ScreenBackground(
        overlay = listOf(
            Color.Transparent,
            NightBase.copy(alpha = 0.60f),
            NightBase
        ),
        flakes = 14,
        flakeAlpha = 0.14f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onSettings,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.cd_settings),
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                StarBadge(stars = state.stars)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                HeroSprites()
            }

            GlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = sheetOffset * this.density
                    }
                    .alpha(sheetAlpha),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                container = SurfaceSheet.copy(alpha = 0.94f),
                borderColor = TextPrimary.copy(alpha = 0.10f),
                elevation = 18.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.app_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.menu_tagline, state.currentYard),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    LinearProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .semantics {
                                stateDescription = progressDescription
                            },
                        color = FrostBlue,
                        trackColor = SurfaceMuted,
                        drawStopIndicator = {}
                    )
                    if (state.showStats) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                value = state.bestStars.toString(),
                                label = stringResource(R.string.best_stars_label),
                                valueColor = MittenOrange,
                                icon = Icons.Default.Star,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                value = stringResource(R.string.percent_value, state.bestAccuracy),
                                label = stringResource(R.string.best_accuracy_label),
                                valueColor = FrostBlue,
                                icon = Icons.Default.Check,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    PrimaryButton(
                        text = stringResource(R.string.play),
                        onClick = onPlay,
                        icon = Icons.Default.PlayArrow,
                        height = 62.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SecondaryButton(
                            text = stringResource(R.string.fort),
                            onClick = onFort,
                            icon = Icons.Default.Build,
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = stringResource(R.string.mittens),
                            onClick = onShop,
                            icon = Icons.Default.ShoppingCart,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroSprites() {
    val transition = rememberInfiniteTransition(label = "hero")
    val wobble by transition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroWobble"
    )
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.sprite_2),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 28.dp, bottom = 24.dp)
                .size(56.dp)
                .graphicsLayer { rotationZ = -wobble }
        )
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 30.dp, bottom = 10.dp)
                .size(108.dp)
                .graphicsLayer { rotationZ = wobble }
        )
        Image(
            painter = painterResource(R.drawable.sprite_3),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 10.dp)
                .width(150.dp)
                .height(74.dp)
                .alpha(0.9f)
        )
    }
}

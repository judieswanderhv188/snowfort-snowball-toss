package com.snowfort.toss.presentation.gameover

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snowfort.toss.R
import com.snowfort.toss.core.di.AppViewModelFactory
import com.snowfort.toss.core.theme.FrostBlue
import com.snowfort.toss.core.theme.MittenOrange
import com.snowfort.toss.core.theme.NightAbyss
import com.snowfort.toss.core.theme.NightBase
import com.snowfort.toss.core.theme.SurfaceRaised
import com.snowfort.toss.core.theme.SurfaceSheet
import com.snowfort.toss.core.theme.TextPrimary
import com.snowfort.toss.core.theme.TextSecondary
import com.snowfort.toss.core.theme.VioletPrimary
import com.snowfort.toss.core.ui.GlassSurface
import com.snowfort.toss.core.ui.PrimaryButton
import com.snowfort.toss.core.ui.ScreenBackground
import com.snowfort.toss.core.ui.SecondaryButton
import com.snowfort.toss.core.ui.StarRow
import com.snowfort.toss.core.ui.StatCard

private data class ResultStat(
    val value: String,
    val label: String,
    val color: Color,
    val icon: ImageVector
)

@Composable
fun GameOverScreen(
    yard: Int,
    hits: Int,
    shots: Int,
    stars: Int,
    cleared: Boolean,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit,
    viewModel: GameOverViewModel = viewModel(factory = AppViewModelFactory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        viewModel.refresh()
        onDispose { }
    }

    GameOverContent(
        yard = yard,
        hits = hits,
        shots = shots,
        stars = stars,
        cleared = cleared,
        totalStars = state.totalStars,
        onPlayAgain = onPlayAgain,
        onMenu = onMenu
    )
}

@Composable
private fun GameOverContent(
    yard: Int,
    hits: Int,
    shots: Int,
    stars: Int,
    cleared: Boolean,
    totalStars: Int,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val accuracy = if (shots <= 0) 0 else (hits * 100) / shots
    val cardScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "resultCard"
    )

    val hitsValue = stringResource(R.string.hits_value, hits, shots.coerceAtLeast(1))
    val hitsLabel = stringResource(R.string.hits_label)
    val accuracyValue = stringResource(R.string.percent_value, accuracy)
    val accuracyLabel = stringResource(R.string.accuracy_label)
    val starsValue = stringResource(R.string.plus_value, stars)
    val starsLabel = stringResource(R.string.stars_label)

    val statList = mutableListOf<ResultStat>()
    if (hits > 0) {
        statList.add(
            ResultStat(hitsValue, hitsLabel, MittenOrange, Icons.Default.Star)
        )
    }
    if (accuracy > 0) {
        statList.add(
            ResultStat(accuracyValue, accuracyLabel, FrostBlue, Icons.Default.Check)
        )
    }
    if (stars > 0) {
        statList.add(
            ResultStat(starsValue, starsLabel, TextPrimary, Icons.Default.Star)
        )
    }

    ScreenBackground(
        overlay = listOf(
            NightBase.copy(alpha = 0.95f),
            SurfaceRaised.copy(alpha = 0.90f),
            NightAbyss.copy(alpha = 0.95f)
        ),
        flakes = 10,
        flakeAlpha = 0.12f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    VioletPrimary.copy(alpha = 0.30f),
                                    VioletPrimary.copy(alpha = 0f)
                                )
                            )
                        )
                )
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = cardScale
                            scaleY = cardScale
                        },
                    shape = RoundedCornerShape(24.dp),
                    container = SurfaceSheet.copy(alpha = 0.96f),
                    borderColor = TextPrimary.copy(alpha = 0.12f),
                    elevation = 20.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (stars >= 3) {
                            Image(
                                painter = painterResource(R.drawable.sprite_4),
                                contentDescription = stringResource(R.string.cd_trophy),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        Text(
                            text = if (cleared) {
                                stringResource(R.string.yard_cleared)
                            } else {
                                stringResource(R.string.out_of_snowballs)
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (cleared) TextPrimary else TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.yard_label, yard),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StarRow(
                            earned = stars,
                            total = 3
                        )
                        if (statList.size >= 2) {
                            Spacer(modifier = Modifier.height(18.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                statList.forEach { stat ->
                                    StatCard(
                                        value = stat.value,
                                        label = stat.label,
                                        valueColor = stat.color,
                                        icon = stat.icon,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(22.dp))
                        PrimaryButton(
                            text = stringResource(R.string.play_again),
                            onClick = onPlayAgain,
                            icon = Icons.Default.Refresh,
                            height = 60.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SecondaryButton(
                            text = stringResource(R.string.menu),
                            onClick = onMenu,
                            iconPainter = painterResource(R.drawable.icon_home)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = stringResource(R.string.total_stars, totalStars),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

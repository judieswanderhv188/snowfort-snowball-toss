package com.snowfort.toss.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snowfort.toss.R
import com.snowfort.toss.core.di.AppViewModelFactory
import com.snowfort.toss.core.theme.FrostBlue
import com.snowfort.toss.core.theme.MittenOrange
import com.snowfort.toss.core.theme.NightAbyss
import com.snowfort.toss.core.theme.NightBase
import com.snowfort.toss.core.theme.NightDeep
import com.snowfort.toss.core.theme.TextMuted
import com.snowfort.toss.core.theme.TextPrimary
import com.snowfort.toss.core.theme.TextSecondary
import com.snowfort.toss.core.theme.VioletPrimary
import com.snowfort.toss.core.theme.VioletSecondary
import com.snowfort.toss.core.ui.ScreenBackground

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    viewModel: SplashViewModel = viewModel(factory = AppViewModelFactory)
) {
    val finished by viewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) {
            onFinished()
        }
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    val transition = rememberInfiniteTransition(label = "splash")
    val float by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoFloat"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoBreathe"
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600),
        label = "taglineAlpha"
    )

    ScreenBackground(
        overlay = listOf(
            NightBase.copy(alpha = 0.90f),
            NightDeep.copy(alpha = 0.95f),
            NightAbyss.copy(alpha = 0.98f)
        ),
        flakes = 24,
        flakeAlpha = 0.22f
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(280.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            VioletPrimary.copy(alpha = 0.22f),
                            VioletPrimary.copy(alpha = 0f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(148.dp)
                    .graphicsLayer {
                        translationY = float * this.density
                        scaleX = breathe
                        scaleY = breathe
                    }
            )
            Box(modifier = Modifier.height(24.dp))
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.app_title),
                    style = MaterialTheme.typography.displaySmall,
                    color = VioletPrimary,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.graphicsLayer {
                        translationX = 2f * this.density
                        translationY = 2f * this.density
                    }
                )
                Text(
                    text = stringResource(R.string.app_title),
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black
                )
            }
            Box(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary,
                modifier = Modifier.alpha(taglineAlpha)
            )
            Box(modifier = Modifier.height(28.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MittenOrange,
                trackColor = VioletSecondary
            )
            Box(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                letterSpacing = 4.sp
            )
            Box(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(FrostBlue.copy(alpha = 0.25f))
            )
        }
    }
}

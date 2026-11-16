package com.snowfort.toss.presentation.shop

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.snowfort.toss.core.theme.SurfaceRaised
import com.snowfort.toss.core.theme.SurfaceSheet
import com.snowfort.toss.core.theme.TextMuted
import com.snowfort.toss.core.theme.TextPrimary
import com.snowfort.toss.core.theme.TextSecondary
import com.snowfort.toss.core.theme.VioletPrimary
import com.snowfort.toss.core.ui.ScreenBackground
import com.snowfort.toss.core.ui.SecondaryButton
import com.snowfort.toss.core.ui.SectionHeader
import com.snowfort.toss.core.ui.StarBadge
import com.snowfort.toss.domain.model.Mitten

@Composable
fun MittenShopScreen(
    onBack: () -> Unit,
    viewModel: MittenShopViewModel = viewModel(factory = AppViewModelFactory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        viewModel.refresh()
        onDispose { }
    }

    MittenShopContent(
        state = state,
        onBack = onBack,
        onSelect = { viewModel.select(it) }
    )
}

@Composable
private fun MittenShopContent(
    state: MittenShopUiState,
    onBack: () -> Unit,
    onSelect: (Mitten) -> Unit
) {
    ScreenBackground(
        overlay = listOf(
            NightBase.copy(alpha = 0.90f),
            SurfaceSheet.copy(alpha = 0.93f),
            NightBase.copy(alpha = 0.96f)
        ),
        flakes = 12,
        flakeAlpha = 0.12f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            SectionHeader(
                title = stringResource(R.string.mitten_shop),
                onBack = onBack,
                trailing = { StarBadge(stars = state.stars) }
            )
            Text(
                text = stringResource(R.string.shop_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.mittens, key = { it.id }) { mitten ->
                    MittenCard(
                        mitten = mitten,
                        owned = state.isOwned(mitten),
                        equipped = state.isEquipped(mitten),
                        affordable = state.canAfford(mitten),
                        onSelect = { onSelect(mitten) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MittenCard(
    mitten: Mitten,
    owned: Boolean,
    equipped: Boolean,
    affordable: Boolean,
    onSelect: () -> Unit
) {
    val tint = Color(mitten.tint)
    val ownedLabel = stringResource(R.string.state_owned)
    val equippedLabel = stringResource(R.string.state_equipped)
    val blockedLabel = stringResource(R.string.state_not_enough)
    val enabled = owned || affordable
    val description = when {
        equipped -> equippedLabel
        owned -> ownedLabel
        affordable -> ownedLabel
        else -> blockedLabel
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.5f)
            .semantics { stateDescription = description },
        shape = RoundedCornerShape(16.dp),
        color = if (equipped) VioletPrimary.copy(alpha = 0.55f) else SurfaceRaised,
        border = BorderStroke(
            1.dp,
            if (owned) FrostBlue.copy(alpha = 0.5f) else TextPrimary.copy(alpha = 0.10f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(tint),
                    modifier = Modifier.size(54.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mitten.name,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (owned) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = FrostBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (equipped) equippedLabel else ownedLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MittenOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = mitten.price.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MittenOrange
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = when {
                    equipped -> equippedLabel
                    owned -> stringResource(R.string.equip)
                    else -> stringResource(R.string.buy)
                },
                onClick = onSelect,
                enabled = enabled && !equipped,
                height = 48.dp
            )
        }
    }
}

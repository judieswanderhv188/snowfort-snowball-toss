package com.snowfort.toss.presentation.fort

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.snowfort.toss.core.config.GameConfig
import com.snowfort.toss.core.di.AppViewModelFactory
import com.snowfort.toss.core.theme.FrostBlue
import com.snowfort.toss.core.theme.MittenOrange
import com.snowfort.toss.core.theme.NightBase
import com.snowfort.toss.core.theme.SurfaceMuted
import com.snowfort.toss.core.theme.SurfaceRaised
import com.snowfort.toss.core.theme.SurfaceSheet
import com.snowfort.toss.core.theme.TextMuted
import com.snowfort.toss.core.theme.TextPrimary
import com.snowfort.toss.core.theme.TextSecondary
import com.snowfort.toss.core.theme.VioletSecondary
import com.snowfort.toss.core.ui.GlassSurface
import com.snowfort.toss.core.ui.PrimaryButton
import com.snowfort.toss.core.ui.ScreenBackground
import com.snowfort.toss.core.ui.SectionHeader
import com.snowfort.toss.core.ui.StarBadge
import com.snowfort.toss.domain.model.FortPiece

@Composable
fun FortBuilderScreen(
    onBack: () -> Unit,
    viewModel: FortBuilderViewModel = viewModel(factory = AppViewModelFactory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        viewModel.refresh()
        onDispose { }
    }

    LaunchedEffect(Unit) {
        viewModel.completed.collect {
            onBack()
        }
    }

    FortBuilderContent(
        state = state,
        onBack = onBack,
        onSelect = { viewModel.selectPiece(it) },
        onPlace = { viewModel.placeAt(it) },
        onSave = { viewModel.save() }
    )
}

@Composable
private fun FortBuilderContent(
    state: FortBuilderUiState,
    onBack: () -> Unit,
    onSelect: (Int) -> Unit,
    onPlace: (Int) -> Unit,
    onSave: () -> Unit
) {
    ScreenBackground(
        overlay = listOf(
            NightBase.copy(alpha = 0.88f),
            SurfaceSheet.copy(alpha = 0.92f),
            NightBase.copy(alpha = 0.95f)
        ),
        flakes = 12,
        flakeAlpha = 0.14f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionHeader(
                title = stringResource(R.string.fort_builder),
                onBack = onBack,
                trailing = { StarBadge(stars = state.stars) }
            )
            Text(
                text = stringResource(R.string.fort_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            BuildGrid(
                layout = state.layout,
                pieces = state.pieces,
                onPlace = onPlace
            )
            Spacer(modifier = Modifier.height(20.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.pieces, key = { it.id }) { piece ->
                    PieceChip(
                        piece = piece,
                        unlocked = state.unlocked(piece),
                        selected = state.selectedPiece == piece.id,
                        onSelect = { onSelect(piece.id) }
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(
                text = stringResource(R.string.save_fort),
                onClick = onSave,
                height = 58.dp
            )
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun BuildGrid(
    layout: List<Int>,
    pieces: List<FortPiece>,
    onPlace: (Int) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val frame = GameConfig.FORT_BOARD_PAD_DP + GameConfig.FORT_BOARD_BORDER_DP
        val available = minOf(maxWidth.value.toInt(), GameConfig.FORT_BOARD_MAX_DP)
        val tile = (available - 2 * frame) / GameConfig.FORT_GRID
        val board = tile * GameConfig.FORT_GRID + 2 * frame
        GlassSurface(
            modifier = Modifier
                .width(board.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(20.dp),
            container = SurfaceRaised.copy(alpha = 0.92f),
            borderColor = TextPrimary.copy(alpha = 0.12f),
            elevation = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(GameConfig.FORT_BOARD_PAD_DP.dp)
            ) {
                repeat(GameConfig.FORT_GRID) { row ->
                    Row {
                        repeat(GameConfig.FORT_GRID) { column ->
                            val slot = row * GameConfig.FORT_GRID + column
                            val pieceId = layout.getOrElse(slot) { -1 }
                            val piece = pieces.firstOrNull { it.id == pieceId }
                            Box(
                                modifier = Modifier
                                    .size(tile.dp)
                                    .padding(3.dp)
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { onPlace(slot) },
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (piece != null) {
                                        VioletSecondary.copy(alpha = 0.55f)
                                    } else {
                                        SurfaceMuted
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        if (piece != null) {
                                            FrostBlue.copy(alpha = 0.45f)
                                        } else {
                                            TextPrimary.copy(alpha = 0.10f)
                                        }
                                    )
                                ) {
                                    if (piece != null) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                painter = painterResource(spriteFor(piece.spriteKey)),
                                                contentDescription = null,
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier.size(44.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PieceChip(
    piece: FortPiece,
    unlocked: Boolean,
    selected: Boolean,
    onSelect: () -> Unit
) {
    val lockedLabel = stringResource(R.string.state_locked)
    Surface(
        modifier = Modifier
            .size(width = 86.dp, height = 96.dp)
            .alpha(if (unlocked) 1f else 0.45f)
            .clickable(enabled = unlocked) { onSelect() }
            .semantics {
                if (!unlocked) {
                    stateDescription = lockedLabel
                }
            },
        shape = RoundedCornerShape(16.dp),
        color = if (selected) VioletSecondary.copy(alpha = 0.8f) else SurfaceRaised,
        border = BorderStroke(
            1.dp,
            if (selected) MittenOrange.copy(alpha = 0.7f) else TextPrimary.copy(alpha = 0.10f)
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(spriteFor(piece.spriteKey)),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (unlocked) piece.name else lockedLabel,
                style = MaterialTheme.typography.labelSmall,
                color = if (unlocked) TextSecondary else TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun spriteFor(key: String): Int = when (key) {
    "banner" -> R.drawable.sprite_4
    "ice" -> R.drawable.sprite_5
    else -> R.drawable.sprite_3
}

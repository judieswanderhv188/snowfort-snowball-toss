package com.snowfort.toss.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.snowfort.toss.core.theme.MittenOrange
import com.snowfort.toss.core.theme.NightBase
import com.snowfort.toss.core.theme.SurfaceMuted
import com.snowfort.toss.core.theme.SurfaceSheet
import com.snowfort.toss.core.theme.TextMuted
import com.snowfort.toss.core.theme.TextPrimary
import com.snowfort.toss.core.theme.TextSecondary
import com.snowfort.toss.core.theme.VioletPrimary
import com.snowfort.toss.core.ui.ScreenBackground
import com.snowfort.toss.core.ui.SectionHeader
import com.snowfort.toss.presentation.dialog.ConfirmDialog

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        viewModel.refresh()
        onDispose { }
    }

    SettingsContent(
        state = state,
        onBack = onBack,
        onSound = { viewModel.setSound(it) },
        onHaptics = { viewModel.setHaptics(it) },
        onAssist = { viewModel.setArcAssist(it) },
        onResetRequest = { viewModel.requestReset() },
        onResetConfirm = { viewModel.confirmReset() },
        onResetDismiss = { viewModel.dismissReset() }
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onBack: () -> Unit,
    onSound: (Boolean) -> Unit,
    onHaptics: (Boolean) -> Unit,
    onAssist: (Boolean) -> Unit,
    onResetRequest: () -> Unit,
    onResetConfirm: () -> Unit,
    onResetDismiss: () -> Unit
) {
    ScreenBackground(
        overlay = listOf(
            NightBase.copy(alpha = 0.92f),
            SurfaceSheet.copy(alpha = 0.94f),
            NightBase.copy(alpha = 0.97f)
        ),
        flakes = 10,
        flakeAlpha = 0.12f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            SectionHeader(
                title = stringResource(R.string.settings),
                onBack = onBack
            )
            Spacer(modifier = Modifier.height(8.dp))
            ToggleRow(
                label = stringResource(R.string.sound_label),
                checked = state.sound,
                onChange = onSound
            )
            Spacer(modifier = Modifier.height(10.dp))
            ToggleRow(
                label = stringResource(R.string.haptics_label),
                checked = state.haptics,
                onChange = onHaptics
            )
            Spacer(modifier = Modifier.height(10.dp))
            ToggleRow(
                label = stringResource(R.string.assist_label),
                checked = state.arcAssist,
                onChange = onAssist
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                onClick = onResetRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MittenOrange)
            ) {
                Text(
                    text = stringResource(R.string.reset_progress),
                    style = MaterialTheme.typography.titleSmall,
                    color = MittenOrange
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.cd_logo),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(84.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.about_label),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (state.confirmVisible) {
            ConfirmDialog(
                title = stringResource(R.string.reset_progress),
                message = stringResource(R.string.reset_message),
                onConfirm = onResetConfirm,
                onDismiss = onResetDismiss
            )
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    val onLabel = stringResource(R.string.state_on)
    val offLabel = stringResource(R.string.state_off)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .semantics { stateDescription = if (checked) onLabel else offLabel },
        shape = RoundedCornerShape(16.dp),
        color = SurfaceSheet.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, TextPrimary.copy(alpha = 0.10f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = TextSecondary
            )
            Switch(
                checked = checked,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TextPrimary,
                    checkedTrackColor = MittenOrange,
                    checkedBorderColor = MittenOrange,
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = SurfaceMuted,
                    uncheckedBorderColor = VioletPrimary
                )
            )
        }
    }
}

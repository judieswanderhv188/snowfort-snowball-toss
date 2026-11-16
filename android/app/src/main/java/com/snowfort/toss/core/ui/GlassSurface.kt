package com.snowfort.toss.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.snowfort.toss.core.theme.SurfaceSheet
import com.snowfort.toss.core.theme.TextPrimary
import com.snowfort.toss.core.theme.VioletPrimary

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    container: Color = SurfaceSheet.copy(alpha = 0.94f),
    borderColor: Color = TextPrimary.copy(alpha = 0.10f),
    shadowColor: Color = VioletPrimary,
    elevation: Dp = 18.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = shadowColor,
            spotColor = shadowColor
        ),
        shape = shape,
        color = container,
        border = BorderStroke(1.dp, borderColor),
        content = content
    )
}

package musicapp.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val lightPrimaryLighter = Color(0xFF8F89F8)
val lightPrimary = Color(0xFF605ca8)

val gradientBrush = Brush.linearGradient(
    colors = listOf(lightPrimaryLighter, lightPrimary),
    start = Offset(0f, 0f),
    end = Offset(0f, Float.POSITIVE_INFINITY)
)

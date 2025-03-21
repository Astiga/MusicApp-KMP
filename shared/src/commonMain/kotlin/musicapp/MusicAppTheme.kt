package musicapp

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun MusicAppTheme(
    content: @Composable () -> Unit
) {
    val colors = darkColorScheme(
        primary = Color(0xFF1D2123),
        secondary = Color(0xFFFACD66),
        surface = Color(0xFF1E1E1E),
        background = Color(0xFF1E1E1E),
        onSurface = Color(0xFF1E1E1E),
    )

    val typography = Typography(
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 16.sp
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    MaterialTheme(
        colorScheme = colors, typography = typography, shapes = shapes, content = content
    )
}

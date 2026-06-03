package droga_krzyzowa.droga_krzyzowa.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    secondary = GoldLight,
    tertiary = DeepPurple,
    background = DeepPurple,
    surface = DeepPurple
)

@Composable
fun DrogaKrzyzowaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
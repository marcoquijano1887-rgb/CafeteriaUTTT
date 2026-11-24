package mx.uttt.cafeuttt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CafeColorScheme = lightColorScheme(
    primary = Terracota,
    onPrimary = Color.White,
    primaryContainer = Durazno,
    onPrimaryContainer = TerracotaOscuro,
    secondary = Ambar,
    onSecondary = Tinta,
    secondaryContainer = Durazno,
    onSecondaryContainer = TerracotaOscuro,
    tertiary = VerdeAvance,
    background = Crema,
    onBackground = Tinta,
    surface = CremaTarjeta,
    onSurface = Tinta,
    surfaceVariant = Durazno,
    onSurfaceVariant = TintaSuave,
    outline = Borde,
    outlineVariant = Borde,
    error = RojoError
)

/** Tema Material 3 de CafeUTTT (sin color dinámico para respetar la identidad visual). */
@Composable
fun CafeUTTTTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CafeColorScheme, content = content)
}

package ru.avem.viu35.themes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
)

private val LightColorPalette = lightColors(
)

@Composable
fun BarColorsTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = MaterialTheme.typography,
        shapes = Shapes(),
        content = content
    )
    CheckboxDefaults.colors()
}
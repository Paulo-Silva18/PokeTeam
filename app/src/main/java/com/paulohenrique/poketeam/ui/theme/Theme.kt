package com.paulohenrique.poketeam.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PokeColors = lightColorScheme(
    primary = Color(0xFFD32F2F),
    onPrimary = Color.White,
    secondary = Color(0xFF3B5BA9),
    onSecondary = Color.White,
    tertiary = Color(0xFFFFCB05),
    background = Color(0xFFFAFAFA),
    surface = Color.White
)

@Composable
fun PokeTeamTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PokeColors, content = content)
}

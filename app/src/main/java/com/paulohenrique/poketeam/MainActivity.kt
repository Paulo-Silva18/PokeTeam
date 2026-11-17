package com.paulohenrique.poketeam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.paulohenrique.poketeam.ui.navigation.PokeTeamNavHost
import com.paulohenrique.poketeam.ui.theme.PokeTeamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as PokeTeamApp).container
        setContent {
            PokeTeamTheme {
                PokeTeamNavHost(container)
            }
        }
    }
}

package com.paulohenrique.poketeam.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paulohenrique.poketeam.ui.components.CenterMessage
import com.paulohenrique.poketeam.ui.components.PokemonRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(vm: FavoritesViewModel, onOpenDetail: (Int) -> Unit) {
    val favorites by vm.favorites.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Favoritos", fontWeight = FontWeight.Bold) })

        if (favorites.isEmpty()) {
            CenterMessage(
                Icons.Filled.FavoriteBorder,
                "Nenhum favorito ainda",
                "Abra um Pokémon e toque em Favoritar. Eles ficam salvos mesmo sem internet."
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(favorites, key = { it.id }) { p ->
                    PokemonRow(pokemon = p, onClick = { onOpenDetail(p.id) }) {
                        IconButton(onClick = { vm.remove(p.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Remover dos favoritos")
                        }
                    }
                }
            }
        }
    }
}

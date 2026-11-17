package com.paulohenrique.poketeam.ui.pokedex

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paulohenrique.poketeam.ui.components.CenterMessage
import com.paulohenrique.poketeam.ui.components.PokemonCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokedexScreen(
    vm: PokedexViewModel,
    onOpenDetail: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    val searching = state.query.isNotBlank()
    val shown = if (searching) state.results else state.items

    // Paginação infinita: pede a próxima página quando faltam ~4 itens para o fim.
    val nearEnd by remember {
        derivedStateOf {
            val info = gridState.layoutInfo
            val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
            last >= info.totalItemsCount - 4
        }
    }
    LaunchedEffect(nearEnd, state.items.size) {
        if (nearEnd) vm.loadNext()
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Pokédex", fontWeight = FontWeight.Bold) },
            actions = {
                IconButton(onClick = onLogout) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sair")
                }
            }
        )
        OutlinedTextField(
            value = state.query,
            onValueChange = vm::onQueryChange,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Buscar por nome ou número") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (searching) {
                    IconButton(onClick = { vm.onQueryChange("") }) {
                        Icon(Icons.Filled.Close, contentDescription = "Limpar busca")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        when {
            shown.isEmpty() && (state.isLoading || state.isSearching) ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            shown.isEmpty() && state.error != null ->
                CenterMessage(Icons.Filled.CloudOff, "Sem conexão", state.error) {
                    Button(onClick = vm::retry) { Text("Tentar novamente") }
                }

            shown.isEmpty() && searching ->
                CenterMessage(
                    Icons.Filled.SearchOff,
                    "Nenhum Pokémon encontrado",
                    "Tente outro nome ou o número da Pokédex."
                )

            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = gridState,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(shown, key = { it.id }) { p ->
                    PokemonCard(
                        id = p.id,
                        name = p.name,
                        imageUrl = p.imageUrl,
                        isFavorite = p.id in state.favoriteIds,
                        onClick = { onOpenDetail(p.id) }
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when {
                            state.isLoading || state.isSearching -> CircularProgressIndicator()
                            state.error != null -> {
                                Text(state.error!!, color = MaterialTheme.colorScheme.error)
                                TextButton(onClick = vm::retry) { Text("Tentar de novo") }
                            }
                        }
                    }
                }
            }
        }
    }
}

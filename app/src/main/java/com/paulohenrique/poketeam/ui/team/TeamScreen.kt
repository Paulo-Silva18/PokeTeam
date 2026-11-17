package com.paulohenrique.poketeam.ui.team

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.paulohenrique.poketeam.model.MAX_TEAM_SIZE
import com.paulohenrique.poketeam.model.PokemonDetail
import com.paulohenrique.poketeam.ui.components.TypeChip
import com.paulohenrique.poketeam.util.prettyName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(vm: TeamViewModel, onOpenDetail: (Int) -> Unit) {
    val team by vm.team.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Meu time (${team.size}/$MAX_TEAM_SIZE)", fontWeight = FontWeight.Bold) })

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(count = MAX_TEAM_SIZE) { index ->
                val pokemon = team.getOrNull(index)
                if (pokemon != null) {
                    TeamSlot(pokemon, onClick = { onOpenDetail(pokemon.id) }, onRemove = { vm.remove(pokemon.id) })
                } else {
                    EmptySlot(index + 1)
                }
            }
        }
    }
}

@Composable
private fun TeamSlot(pokemon: PokemonDetail, onClick: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(210.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(model = pokemon.imageUrl, contentDescription = pokemon.name, modifier = Modifier.size(96.dp))
            Text(
                pokemon.name.prettyName(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                pokemon.types.forEach { TypeChip(it) }
            }
            TextButton(onClick = onRemove) { Text("Remover") }
        }
    }
}

@Composable
private fun EmptySlot(number: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(210.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                Text("Slot $number vazio", color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

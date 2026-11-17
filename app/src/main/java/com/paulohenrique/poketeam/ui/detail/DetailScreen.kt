package com.paulohenrique.poketeam.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.GroupRemove
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.paulohenrique.poketeam.model.PokemonDetail
import com.paulohenrique.poketeam.ui.components.CenterMessage
import com.paulohenrique.poketeam.ui.components.TypeChip
import com.paulohenrique.poketeam.ui.components.statLabel
import com.paulohenrique.poketeam.ui.components.typeColor
import com.paulohenrique.poketeam.util.prettyName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(vm: DetailViewModel, onBack: () -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            vm.consumeMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text(state.pokemon?.name?.prettyName() ?: "Detalhes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            val pokemon = state.pokemon
            when {
                state.loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                pokemon == null -> CenterMessage(Icons.Filled.CloudOff, "Sem conexão", state.error) {
                    Button(onClick = vm::load) { Text("Tentar novamente") }
                }
                else -> DetailContent(
                    pokemon = pokemon,
                    isFavorite = state.isFavorite,
                    inTeam = state.inTeam,
                    onToggleFavorite = vm::toggleFavorite,
                    onToggleTeam = vm::toggleTeam
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    pokemon: PokemonDetail,
    isFavorite: Boolean,
    inTeam: Boolean,
    onToggleFavorite: () -> Unit,
    onToggleTeam: () -> Unit
) {
    val mainColor = typeColor(pokemon.types.firstOrNull() ?: "")

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(mainColor.copy(alpha = 0.18f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier.size(220.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "#%03d".format(pokemon.id),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            pokemon.name.prettyName(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            pokemon.types.forEach { TypeChip(it) }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Info("Altura", "%.1f m".format(pokemon.heightMeters))
            Info("Peso", "%.1f kg".format(pokemon.weightKg))
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "Status base",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(8.dp))
        pokemon.stats.forEach { (name, value) ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(statLabel(name), modifier = Modifier.width(96.dp), style = MaterialTheme.typography.bodyMedium)
                Text("$value", modifier = Modifier.width(36.dp), fontWeight = FontWeight.SemiBold)
                LinearProgressIndicator(
                    progress = { (value / 200f).coerceAtMost(1f) },
                    color = mainColor,
                    modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp))
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onToggleFavorite, modifier = Modifier.weight(1f)) {
                Icon(
                    if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(if (isFavorite) "Favoritado" else "Favoritar")
            }
            Button(onClick = onToggleTeam, modifier = Modifier.weight(1f)) {
                Icon(
                    if (inTeam) Icons.Filled.GroupRemove else Icons.Filled.GroupAdd,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(if (inTeam) "Sair do time" else "Pôr no time")
            }
        }
    }
}

@Composable
private fun Info(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
    }
}

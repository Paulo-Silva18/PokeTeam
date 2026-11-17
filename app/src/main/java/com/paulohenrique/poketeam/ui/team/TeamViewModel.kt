package com.paulohenrique.poketeam.ui.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulohenrique.poketeam.data.repository.PokemonRepository
import com.paulohenrique.poketeam.model.PokemonDetail
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TeamViewModel(private val repo: PokemonRepository) : ViewModel() {

    val team: StateFlow<List<PokemonDetail>> = repo.observeTeam()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun remove(id: Int) {
        viewModelScope.launch { repo.removeFromTeam(id) }
    }
}

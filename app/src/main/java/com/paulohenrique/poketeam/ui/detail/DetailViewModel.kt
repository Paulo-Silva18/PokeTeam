package com.paulohenrique.poketeam.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulohenrique.poketeam.data.repository.PokemonRepository
import com.paulohenrique.poketeam.model.AddToTeamResult
import com.paulohenrique.poketeam.model.MAX_TEAM_SIZE
import com.paulohenrique.poketeam.model.PokemonDetail
import com.paulohenrique.poketeam.util.prettyName
import com.paulohenrique.poketeam.util.safeCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val loading: Boolean = true,
    val pokemon: PokemonDetail? = null,
    val error: String? = null,
    val isFavorite: Boolean = false,
    val inTeam: Boolean = false,
    val message: String? = null
)

class DetailViewModel(
    private val id: Int,
    private val repo: PokemonRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DetailUiState())
    val state: StateFlow<DetailUiState> = _state.asStateFlow()

    init {
        load()
        viewModelScope.launch {
            combine(repo.observeFavoriteIds(), repo.observeTeamIds()) { fav, team ->
                (id in fav) to (id in team)
            }.collect { (fav, team) ->
                _state.update { it.copy(isFavorite = fav, inTeam = team) }
            }
        }
    }

    fun load() {
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            safeCall { repo.getDetail(id) }
                .onSuccess { p -> _state.update { it.copy(loading = false, pokemon = p) } }
                .onFailure {
                    _state.update {
                        it.copy(loading = false, error = "Não foi possível carregar este Pokémon.")
                    }
                }
        }
    }

    fun toggleFavorite() {
        val p = _state.value.pokemon ?: return
        viewModelScope.launch { repo.toggleFavorite(p) }
    }

    fun toggleTeam() {
        val p = _state.value.pokemon ?: return
        viewModelScope.launch {
            if (_state.value.inTeam) {
                repo.removeFromTeam(p.id)
                showMessage("${p.name.prettyName()} saiu do time.")
            } else {
                when (repo.addToTeam(p)) {
                    AddToTeamResult.ADDED -> showMessage("${p.name.prettyName()} entrou no time!")
                    AddToTeamResult.TEAM_FULL ->
                        showMessage("Seu time já tem $MAX_TEAM_SIZE Pokémon. Remova um para continuar.")
                    AddToTeamResult.ALREADY_IN_TEAM -> Unit
                }
            }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }

    private fun showMessage(text: String) = _state.update { it.copy(message = text) }
}

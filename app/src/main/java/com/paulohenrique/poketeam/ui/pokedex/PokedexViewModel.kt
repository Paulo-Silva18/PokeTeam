package com.paulohenrique.poketeam.ui.pokedex

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulohenrique.poketeam.data.repository.PokemonRepository
import com.paulohenrique.poketeam.model.PokemonSummary
import com.paulohenrique.poketeam.util.safeCall
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PokedexUiState(
    val items: List<PokemonSummary> = emptyList(),
    val results: List<PokemonSummary> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val endReached: Boolean = false,
    val error: String? = null,
    val favoriteIds: Set<Int> = emptySet()
)

class PokedexViewModel(private val repo: PokemonRepository) : ViewModel() {

    private val _state = MutableStateFlow(PokedexUiState())
    val state: StateFlow<PokedexUiState> = _state.asStateFlow()

    private var offset = 0
    private var searchJob: Job? = null

    init {
        loadNext()
        viewModelScope.launch {
            repo.observeFavoriteIds().collect { ids ->
                _state.update { it.copy(favoriteIds = ids.toSet()) }
            }
        }
    }

    /** Paginação: carrega a próxima página (20 itens) quando o usuário chega perto do fim. */
    fun loadNext() {
        val s = _state.value
        if (s.isLoading || s.endReached || s.query.isNotBlank()) return
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            safeCall { repo.getPage(offset, PAGE_SIZE) }
                .onSuccess { page ->
                    offset += page.items.size
                    _state.update {
                        it.copy(
                            items = it.items + page.items,
                            isLoading = false,
                            endReached = offset >= page.total || page.items.isEmpty()
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, error = LOAD_ERROR) }
                }
        }
    }

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query, error = null) }
        if (query.isBlank()) {
            searchJob?.cancel()
            _state.update { it.copy(results = emptyList(), isSearching = false) }
        } else {
            runSearch(query)
        }
    }

    fun retry() {
        val query = _state.value.query
        if (query.isBlank()) loadNext() else runSearch(query)
    }

    /** Busca com debounce de 350 ms para não disparar a cada letra digitada. */
    private fun runSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            _state.update { it.copy(isSearching = true, error = null) }
            safeCall { repo.search(query) }
                .onSuccess { found ->
                    _state.update { it.copy(results = found, isSearching = false) }
                }
                .onFailure {
                    _state.update { it.copy(isSearching = false, error = LOAD_ERROR) }
                }
        }
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val LOAD_ERROR = "Não foi possível carregar. Verifique sua conexão."
    }
}

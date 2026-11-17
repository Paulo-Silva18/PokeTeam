package com.paulohenrique.poketeam.data.repository

import com.paulohenrique.poketeam.data.local.PokemonDao
import com.paulohenrique.poketeam.data.remote.PokeApiService
import com.paulohenrique.poketeam.data.toDomain
import com.paulohenrique.poketeam.data.toFavoriteEntity
import com.paulohenrique.poketeam.data.toSummary
import com.paulohenrique.poketeam.data.toTeamEntity
import com.paulohenrique.poketeam.model.AddToTeamResult
import com.paulohenrique.poketeam.model.MAX_TEAM_SIZE
import com.paulohenrique.poketeam.model.PokemonDetail
import com.paulohenrique.poketeam.model.PokemonPage
import com.paulohenrique.poketeam.model.PokemonSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

/**
 * Única fonte de dados da UI: combina a PokéAPI (rede) com o Room (favoritos e time).
 */
class PokemonRepository(
    private val api: PokeApiService,
    private val dao: PokemonDao
) {
    private var nameIndex: List<PokemonSummary>? = null

    // ---------- Rede ----------
    suspend fun getPage(offset: Int, limit: Int): PokemonPage {
        val response = api.list(limit = limit, offset = offset)
        return PokemonPage(response.results.map { it.toSummary() }, response.count)
    }

    /** A PokéAPI não tem endpoint de busca: baixamos o índice de nomes uma vez e filtramos localmente. */
    suspend fun search(query: String): List<PokemonSummary> {
        val all = nameIndex ?: api.list(limit = 2000, offset = 0)
            .results.map { it.toSummary() }
            .also { nameIndex = it }
        val q = query.trim().lowercase()
        return all.filter { it.name.contains(q) || it.id.toString() == q }.take(60)
    }

    /** Tenta a rede; se estiver offline, usa o snapshot salvo no Room (favorito ou time). */
    suspend fun getDetail(id: Int): PokemonDetail =
        try {
            api.detail(id).toDomain()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            dao.getFavorite(id)?.toDomain()
                ?: dao.getTeamMember(id)?.toDomain()
                ?: throw e
        }

    // ---------- Favoritos ----------
    fun observeFavorites(): Flow<List<PokemonDetail>> =
        dao.observeFavorites().map { list -> list.map { it.toDomain() } }

    fun observeFavoriteIds(): Flow<List<Int>> = dao.observeFavoriteIds()

    suspend fun toggleFavorite(pokemon: PokemonDetail) {
        if (dao.getFavorite(pokemon.id) != null) dao.deleteFavorite(pokemon.id)
        else dao.insertFavorite(pokemon.toFavoriteEntity())
    }

    suspend fun removeFavorite(id: Int) = dao.deleteFavorite(id)

    // ---------- Time ----------
    fun observeTeam(): Flow<List<PokemonDetail>> =
        dao.observeTeam().map { list -> list.map { it.toDomain() } }

    fun observeTeamIds(): Flow<List<Int>> = dao.observeTeamIds()

    suspend fun addToTeam(pokemon: PokemonDetail): AddToTeamResult {
        if (dao.getTeamMember(pokemon.id) != null) return AddToTeamResult.ALREADY_IN_TEAM
        if (dao.teamSize() >= MAX_TEAM_SIZE) return AddToTeamResult.TEAM_FULL
        dao.insertTeamMember(pokemon.toTeamEntity(dao.nextTeamPosition()))
        return AddToTeamResult.ADDED
    }

    suspend fun removeFromTeam(id: Int) = dao.deleteTeamMember(id)
}

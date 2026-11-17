package com.paulohenrique.poketeam.model

const val MAX_TEAM_SIZE = 6

/** Item leve usado na lista paginada (a PokéAPI só devolve nome + url). */
data class PokemonSummary(val id: Int, val name: String) {
    val imageUrl: String get() = artworkUrl(id)
}

/** Detalhe completo. É também o "snapshot" salvo no Room para uso offline. */
data class PokemonDetail(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>,
    val heightDm: Int,
    val weightHg: Int,
    val stats: Map<String, Int>
) {
    val heightMeters: Double get() = heightDm / 10.0
    val weightKg: Double get() = weightHg / 10.0
}

data class PokemonPage(val items: List<PokemonSummary>, val total: Int)

enum class AddToTeamResult { ADDED, ALREADY_IN_TEAM, TEAM_FULL }

fun artworkUrl(id: Int) =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

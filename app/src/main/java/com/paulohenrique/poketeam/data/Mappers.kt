package com.paulohenrique.poketeam.data

import com.paulohenrique.poketeam.data.local.FavoriteEntity
import com.paulohenrique.poketeam.data.local.TeamEntity
import com.paulohenrique.poketeam.data.remote.PokemonDetailDto
import com.paulohenrique.poketeam.data.remote.PokemonRefDto
import com.paulohenrique.poketeam.model.PokemonDetail
import com.paulohenrique.poketeam.model.PokemonSummary
import com.paulohenrique.poketeam.model.artworkUrl

fun PokemonRefDto.toSummary() = PokemonSummary(
    id = url.trimEnd('/').substringAfterLast('/').toInt(),
    name = name
)

fun PokemonDetailDto.toDomain() = PokemonDetail(
    id = id,
    name = name,
    imageUrl = artworkUrl(id),
    types = types.sortedBy { it.slot }.map { it.type.name },
    heightDm = height,
    weightHg = weight,
    stats = stats.associate { it.stat.name to it.baseStat }
)

fun PokemonDetail.toFavoriteEntity() = FavoriteEntity(
    id = id, name = name, imageUrl = imageUrl,
    types = types.joinToString(","), heightDm = heightDm, weightHg = weightHg,
    stats = stats.encode(), addedAt = System.currentTimeMillis()
)

fun PokemonDetail.toTeamEntity(position: Int) = TeamEntity(
    id = id, name = name, imageUrl = imageUrl,
    types = types.joinToString(","), heightDm = heightDm, weightHg = weightHg,
    stats = stats.encode(), position = position
)

fun FavoriteEntity.toDomain() = PokemonDetail(
    id, name, imageUrl, types.splitTypes(), heightDm, weightHg, stats.decodeStats()
)

fun TeamEntity.toDomain() = PokemonDetail(
    id, name, imageUrl, types.splitTypes(), heightDm, weightHg, stats.decodeStats()
)

private fun String.splitTypes() = split(",").filter { it.isNotBlank() }

private fun Map<String, Int>.encode() = entries.joinToString(";") { "${it.key}=${it.value}" }

private fun String.decodeStats(): Map<String, Int> =
    split(";").filter { it.contains("=") }.associate {
        val (key, value) = it.split("=")
        key to value.toInt()
    }

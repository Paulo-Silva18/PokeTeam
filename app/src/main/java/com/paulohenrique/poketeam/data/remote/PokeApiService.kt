package com.paulohenrique.poketeam.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokemon")
    suspend fun list(@Query("limit") limit: Int, @Query("offset") offset: Int): PokemonListDto

    @GET("pokemon/{id}")
    suspend fun detail(@Path("id") id: Int): PokemonDetailDto
}

data class PokemonListDto(val count: Int, val results: List<PokemonRefDto>)
data class PokemonRefDto(val name: String, val url: String)

data class PokemonDetailDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TypeSlotDto>,
    val stats: List<StatSlotDto>
)

data class TypeSlotDto(val slot: Int, val type: NamedDto)
data class StatSlotDto(@SerializedName("base_stat") val baseStat: Int, val stat: NamedDto)
data class NamedDto(val name: String)

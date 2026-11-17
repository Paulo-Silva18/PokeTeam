package com.paulohenrique.poketeam.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.paulohenrique.poketeam.data.local.AppDatabase
import com.paulohenrique.poketeam.data.remote.PokeApiService
import com.paulohenrique.poketeam.data.repository.AuthRepository
import com.paulohenrique.poketeam.data.repository.PokemonRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Injeção de dependência manual (simples e suficiente para o escopo do app). */
class AppContainer(context: Context) {
    private val api: PokeApiService = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PokeApiService::class.java)

    private val database = AppDatabase.get(context)

    val pokemonRepository = PokemonRepository(api, database.pokemonDao())
    val authRepository = AuthRepository(FirebaseAuth.getInstance())
}

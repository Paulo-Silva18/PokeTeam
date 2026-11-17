package com.paulohenrique.poketeam.ui.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT = "forgot"
    const val POKEDEX = "pokedex"
    const val FAVORITES = "favorites"
    const val TEAM = "team"
    const val DETAIL = "detail/{id}"

    fun detail(id: Int) = "detail/$id"

    val AUTH = setOf(LOGIN, REGISTER, FORGOT)
    val TABS = setOf(POKEDEX, FAVORITES, TEAM)
}

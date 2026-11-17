package com.paulohenrique.poketeam

import android.app.Application
import com.paulohenrique.poketeam.di.AppContainer

class PokeTeamApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

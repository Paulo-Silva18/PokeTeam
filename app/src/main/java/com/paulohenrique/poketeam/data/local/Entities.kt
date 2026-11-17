package com.paulohenrique.poketeam.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val types: String,      // "grass,poison"
    val heightDm: Int,
    val weightHg: Int,
    val stats: String,      // "hp=45;attack=49;..."
    val addedAt: Long
)

@Entity(tableName = "team")
data class TeamEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val types: String,
    val heightDm: Int,
    val weightHg: Int,
    val stats: String,
    val position: Int
)

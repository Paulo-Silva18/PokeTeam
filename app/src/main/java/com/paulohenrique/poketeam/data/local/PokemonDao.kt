package com.paulohenrique.poketeam.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    // ----- Favoritos -----
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun observeFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT id FROM favorites")
    fun observeFavoriteIds(): Flow<List<Int>>

    @Query("SELECT * FROM favorites WHERE id = :id")
    suspend fun getFavorite(id: Int): FavoriteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(entity: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteFavorite(id: Int)

    // ----- Time -----
    @Query("SELECT * FROM team ORDER BY position")
    fun observeTeam(): Flow<List<TeamEntity>>

    @Query("SELECT id FROM team")
    fun observeTeamIds(): Flow<List<Int>>

    @Query("SELECT * FROM team WHERE id = :id")
    suspend fun getTeamMember(id: Int): TeamEntity?

    @Query("SELECT COUNT(*) FROM team")
    suspend fun teamSize(): Int

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM team")
    suspend fun nextTeamPosition(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamMember(entity: TeamEntity)

    @Query("DELETE FROM team WHERE id = :id")
    suspend fun deleteTeamMember(id: Int)
}

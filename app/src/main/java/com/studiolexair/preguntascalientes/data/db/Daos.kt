package com.studiolexair.preguntascalientes.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomQuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(q: CustomQuestionEntity): Long

    @Query("SELECT * FROM custom_questions ORDER BY createdAt DESC")
    suspend fun getAll(): List<CustomQuestionEntity>

    @Query("DELETE FROM custom_questions WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT COUNT(*) FROM custom_questions")
    suspend fun count(): Int
}

@Dao
interface StatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(stat: StatCounterEntity)

    @Query("SELECT value FROM stats WHERE `key` = :key")
    suspend fun get(key: String): Long?

    @Query("SELECT * FROM stats")
    suspend fun all(): List<StatCounterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putPlayer(stat: PlayerStatEntity)

    @Query("SELECT * FROM player_stats WHERE name = :name")
    suspend fun getPlayer(name: String): PlayerStatEntity?

    @Query("SELECT * FROM player_stats ORDER BY xp DESC")
    suspend fun allPlayers(): List<PlayerStatEntity>
}

@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlock(a: AchievementEntity): Long

    @Query("SELECT `key` FROM achievements")
    suspend fun unlockedKeys(): List<String>
}

@Dao
interface SavedGameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(game: SavedGameEntity)

    @Query("SELECT * FROM saved_games WHERE id = 1")
    suspend fun load(): SavedGameEntity?

    @Query("DELETE FROM saved_games")
    suspend fun clear()
}

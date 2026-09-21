package com.studiolexair.preguntascalientes.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos local Room (catálogo §40): preguntas personalizadas,
 * estadísticas, logros y partidas guardadas. Offline-first.
 */
@Database(
    entities = [
        CustomQuestionEntity::class,
        StatCounterEntity::class,
        PlayerStatEntity::class,
        AchievementEntity::class,
        SavedGameEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customQuestionDao(): CustomQuestionDao
    abstract fun statsDao(): StatsDao
    abstract fun achievementDao(): AchievementDao
    abstract fun savedGameDao(): SavedGameDao
}

object DatabaseProvider {
    @Volatile private var instance: AppDatabase? = null

    fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
        instance ?: Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "preguntas_calientes.db"
        ).fallbackToDestructiveMigration().build().also { instance = it }
    }
}

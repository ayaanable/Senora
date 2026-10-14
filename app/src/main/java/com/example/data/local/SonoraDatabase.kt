package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteEntity::class,
        PlaylistEntity::class,
        PlaylistItemEntity::class,
        RecentlyPlayedEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SonoraDatabase : RoomDatabase() {

    abstract fun sonoraDao(): SonoraDao

    companion object {
        @Volatile
        private var INSTANCE: SonoraDatabase? = null

        fun getInstance(context: Context): SonoraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SonoraDatabase::class.java,
                    "sonora_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

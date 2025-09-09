package at.aau.morselingo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MorseStatsEntity::class], version = 1, exportSchema = false)
abstract class MorselingoDatabase : RoomDatabase() {
    abstract fun morseStatsDao(): MorseStatsDao

    companion object {
        @Volatile
        private var INSTANCE: MorselingoDatabase? = null

        fun getInstance(context: Context): MorselingoDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MorselingoDatabase::class.java,
                    "morselingo_db"
                ).fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
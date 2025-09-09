package at.aau.morselingo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MorseStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: MorseStatsEntity)

    @Query("SELECT * FROM morse_stats")
    fun getAll(): Flow<List<MorseStatsEntity>>

    @Query("DELETE FROM morse_stats")
    suspend fun deleteAll()
}

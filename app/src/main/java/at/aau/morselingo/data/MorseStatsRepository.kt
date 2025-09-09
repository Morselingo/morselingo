package at.aau.morselingo.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MorseStatsRepository(private val dao: MorseStatsDao) {
    suspend fun insertStats(stats: MorseStats) {
        dao.insert(stats.toEntity())
    }

    fun getStats(): Flow<List<MorseStats>> = dao.getAll().map { list -> list.map { it.toDomain() } }

    suspend fun deleteAllData() {
        dao.deleteAll()
    }
}

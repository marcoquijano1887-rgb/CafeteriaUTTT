package mx.uttt.cafeuttt.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mx.uttt.cafeuttt.data.database.entity.ProteinOptionEntity

@Dao
interface ProteinOptionDao {
    @Insert suspend fun insert(item: ProteinOptionEntity): Long
    @Update suspend fun update(item: ProteinOptionEntity)
    @Delete suspend fun delete(item: ProteinOptionEntity)
    @Query("SELECT * FROM protein_options ORDER BY name") fun observeAll(): Flow<List<ProteinOptionEntity>>
    @Query("SELECT * FROM protein_options WHERE id = :id") fun observeById(id: Int): Flow<ProteinOptionEntity?>
}

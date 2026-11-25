package mx.uttt.cafeuttt.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mx.uttt.cafeuttt.data.database.entity.ProductEntity

@Dao
interface ProductDao {
    @Insert suspend fun insert(item: ProductEntity): Long
    @Update suspend fun update(item: ProductEntity)
    @Delete suspend fun delete(item: ProductEntity)
    @Query("SELECT * FROM products ORDER BY name") fun observeAll(): Flow<List<ProductEntity>>
    @Query("SELECT * FROM products WHERE (:category IS NULL OR category = :category) AND name LIKE '%' || :query || '%' ORDER BY name")
    fun observeByCategoryAndName(category: String?, query: String): Flow<List<ProductEntity>>
    @Query("SELECT * FROM products WHERE id = :id LIMIT 1") fun observeById(id: Int): Flow<ProductEntity?>
}

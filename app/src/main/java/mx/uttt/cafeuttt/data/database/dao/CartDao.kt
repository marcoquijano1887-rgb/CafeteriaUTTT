package mx.uttt.cafeuttt.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mx.uttt.cafeuttt.data.database.entity.CartItemEntity
import mx.uttt.cafeuttt.data.database.entity.ProductEntity
import mx.uttt.cafeuttt.data.database.entity.ProteinOptionEntity

data class CartLine(
    @Embedded val item: CartItemEntity,
    @Relation(parentColumn = "product_id", entityColumn = "id") val product: ProductEntity,
    @Relation(parentColumn = "protein_option_id", entityColumn = "id") val protein: ProteinOptionEntity?
)

@Dao
interface CartDao {
    @Insert suspend fun insert(item: CartItemEntity): Long
    @Update suspend fun update(item: CartItemEntity)
    @Delete suspend fun delete(item: CartItemEntity)
    @Transaction
    @Query("SELECT * FROM cart_items ORDER BY id") fun observeCart(): Flow<List<CartLine>>
    @Query("DELETE FROM cart_items") suspend fun clear()
}

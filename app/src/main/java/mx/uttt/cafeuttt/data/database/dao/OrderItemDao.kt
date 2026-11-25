package mx.uttt.cafeuttt.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mx.uttt.cafeuttt.data.database.entity.OrderItemEntity

@Dao
interface OrderItemDao {
    @Insert suspend fun insert(item: OrderItemEntity): Long
    @Update suspend fun update(item: OrderItemEntity)
    @Delete suspend fun delete(item: OrderItemEntity)
    @Query("SELECT * FROM order_items WHERE order_number = :orderNumber ORDER BY id")
    fun observeForOrder(orderNumber: Int): Flow<List<OrderItemEntity>>
}

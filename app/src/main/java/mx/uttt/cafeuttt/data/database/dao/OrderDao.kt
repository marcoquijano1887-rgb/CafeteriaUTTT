package mx.uttt.cafeuttt.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mx.uttt.cafeuttt.data.database.entity.*

data class OrderWithItems(
    @Embedded val order: OrderEntity,
    @Relation(parentColumn = "number", entityColumn = "order_number") val items: List<OrderItemEntity>
)

@Dao
abstract class OrderDao {
    @Insert protected abstract suspend fun insertOrder(order: OrderEntity): Long
    @Insert protected abstract suspend fun insertItems(items: List<OrderItemEntity>)
    @Query("SELECT * FROM cart_items ORDER BY id") protected abstract suspend fun cartRows(): List<CartItemEntity>
    @Query("SELECT price FROM products WHERE id = :id") protected abstract suspend fun productPrice(id: Int): Int
    @Query("SELECT balance FROM users WHERE id = :id") protected abstract suspend fun userBalance(id: Int): Int
    @Query("UPDATE users SET balance = balance - :amount, balance_updated = :updated WHERE id = :id")
    protected abstract suspend fun debitBalance(id: Int, amount: Int, updated: String)

    @Insert abstract suspend fun insert(order: OrderEntity): Long
    @Update abstract suspend fun update(order: OrderEntity)
    @Delete abstract suspend fun delete(order: OrderEntity)

    @Transaction
    @Query("SELECT * FROM orders WHERE user_id = :userId ORDER BY date DESC, number DESC")
    abstract fun observeForUser(userId: Int): Flow<List<OrderWithItems>>

    @Query("SELECT * FROM orders WHERE user_id = :userId AND status IN ('RECIBIDO','PREPARANDO','LISTO') ORDER BY date DESC, number DESC LIMIT 1")
    abstract fun observeActive(userId: Int): Flow<OrderEntity?>

    @Transaction
    open suspend fun confirmOrder(
        userId: Int,
        date: String,
        pickupTime: String,
        paymentMethod: PaymentMethod,
        discountPercent: Int,
        balanceUpdated: String
    ): Int {
        val cart = cartRows()
        check(cart.isNotEmpty()) { "El carrito está vacío" }
        require(discountPercent in 0..100) { "Porcentaje de descuento fuera de rango" }
        val lines = cart.map { row -> row to (productPrice(row.productId) + row.optionExtra) }
        val subtotal = lines.sumOf { (row, price) -> price * row.qty }
        val discount = subtotal * discountPercent / 100
        val total = subtotal - discount
        if (paymentMethod == PaymentMethod.SALDO_CREDENCIAL) {
            check(userBalance(userId) >= total) { "Saldo insuficiente" }
            debitBalance(userId, total, balanceUpdated)
        }
        val orderNumber = insertOrder(
            OrderEntity(
                userId = userId,
                date = date,
                pickupTime = pickupTime,
                paymentMethod = paymentMethod,
                subtotal = subtotal,
                discount = discount,
                total = total
            )
        ).toInt()
        insertItems(lines.map { (row, unitPrice) ->
            OrderItemEntity(
                orderNumber = orderNumber,
                productId = row.productId,
                proteinOptionId = row.proteinOptionId,
                notes = row.notes,
                qty = row.qty,
                unitPrice = unitPrice
            )
        })
        clearCart()
        return orderNumber
    }

    @Query("DELETE FROM cart_items") protected abstract suspend fun clearCart()
}

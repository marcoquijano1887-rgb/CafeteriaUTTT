package mx.uttt.cafeuttt.domain.repository

import kotlinx.coroutines.flow.Flow
import mx.uttt.cafeuttt.domain.model.*

interface ProductRepository {
    fun observeProducts(category: String? = null, query: String = ""): Flow<List<Product>>
    fun observeProduct(id: Int): Flow<Product?>
    fun observeProteinOptions(): Flow<List<ProteinOption>>
    suspend fun saveProduct(product: Product): Int
    suspend fun deleteProduct(product: Product)
    suspend fun saveProteinOption(option: ProteinOption): Int
    suspend fun deleteProteinOption(option: ProteinOption)
}

interface CartRepository {
    fun observeCart(): Flow<List<CartItem>>
    suspend fun add(item: CartItem): Long
    suspend fun update(item: CartItem)
    suspend fun remove(item: CartItem)
    suspend fun clear()
}

interface OrderRepository {
    fun observeOrders(userId: Int): Flow<List<Order>>
    fun observeActiveOrder(userId: Int): Flow<Order?>
    fun observeItems(orderNumber: Int): Flow<List<OrderItem>>
    suspend fun confirmOrder(
        userId: Int, date: String, pickupTime: String, paymentMethod: PaymentMethod,
        discountPercent: Int, balanceUpdated: String
    ): Int
    suspend fun updateOrder(order: Order)
    suspend fun deleteOrder(order: Order)
}

interface UserRepository {
    fun observeUser(id: Int): Flow<User?>
    fun authenticate(email: String, password: String): Flow<User?>
    suspend fun register(user: User, password: String): Int
    suspend fun update(user: User)
    suspend fun delete(user: User)
}

interface CouponRepository {
    fun observeActiveCoupon(code: String): Flow<Coupon?>
    suspend fun save(coupon: Coupon)
    suspend fun delete(coupon: Coupon)
}

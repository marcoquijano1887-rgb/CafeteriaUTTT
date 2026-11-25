package mx.uttt.cafeuttt.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import mx.uttt.cafeuttt.data.database.dao.*
import mx.uttt.cafeuttt.data.database.entity.*
import mx.uttt.cafeuttt.data.mapper.*
import mx.uttt.cafeuttt.data.security.PasswordHasher
import mx.uttt.cafeuttt.domain.model.*
import mx.uttt.cafeuttt.domain.repository.*

@Singleton
class ProductRepositoryImp @Inject constructor(
    private val productDao: ProductDao,
    private val proteinOptionDao: ProteinOptionDao
) : ProductRepository {
    override fun observeProducts(category: String?, query: String): Flow<List<Product>> =
        productDao.observeByCategoryAndName(category, query).map { rows -> rows.map { it.toDomain() } }
    override fun observeProduct(id: Int): Flow<Product?> = productDao.observeById(id).map { it?.toDomain() }
    override fun observeProteinOptions(): Flow<List<ProteinOption>> =
        proteinOptionDao.observeAll().map { rows -> rows.map { it.toDomain() } }
    override suspend fun saveProduct(product: Product): Int =
        if (product.id == 0) productDao.insert(product.toEntity()).toInt()
        else { productDao.update(product.toEntity()); product.id }
    override suspend fun deleteProduct(product: Product) = productDao.delete(product.toEntity())
    override suspend fun saveProteinOption(option: ProteinOption): Int =
        if (option.id == 0) proteinOptionDao.insert(option.toEntity()).toInt()
        else { proteinOptionDao.update(option.toEntity()); option.id }
    override suspend fun deleteProteinOption(option: ProteinOption) = proteinOptionDao.delete(option.toEntity())
}

@Singleton
class CartRepositoryImp @Inject constructor(private val cartDao: CartDao) : CartRepository {
    override fun observeCart(): Flow<List<CartItem>> = cartDao.observeCart().map { rows -> rows.map { it.toDomain() } }
    override suspend fun add(item: CartItem): Long = cartDao.insert(item.toEntity().copy(id = 0))
    override suspend fun update(item: CartItem) = cartDao.update(item.toEntity())
    override suspend fun remove(item: CartItem) = cartDao.delete(item.toEntity())
    override suspend fun clear() = cartDao.clear()
}

@Singleton
class OrderRepositoryImp @Inject constructor(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val productDao: ProductDao
) : OrderRepository {
    override fun observeOrders(userId: Int): Flow<List<Order>> =
        orderDao.observeForUser(userId).map { rows -> rows.map { it.toDomain() } }
    override fun observeActiveOrder(userId: Int): Flow<Order?> =
        orderDao.observeActive(userId).map { it?.toDomain() }
    override fun observeItems(orderNumber: Int): Flow<List<OrderItem>> =
        orderItemDao.observeForOrder(orderNumber).map { rows ->
            rows.mapNotNull { item -> productDao.observeById(item.productId).first()?.let { item.toDomain(it) } }
        }
    override suspend fun confirmOrder(
        userId: Int, date: String, pickupTime: String, paymentMethod: mx.uttt.cafeuttt.domain.model.PaymentMethod,
        discountPercent: Int, balanceUpdated: String
    ): Int = orderDao.confirmOrder(
        userId, date, pickupTime,
        mx.uttt.cafeuttt.data.database.entity.PaymentMethod.valueOf(paymentMethod.name),
        discountPercent, balanceUpdated
    )
    override suspend fun updateOrder(order: Order) = orderDao.update(order.toEntity())
    override suspend fun deleteOrder(order: Order) = orderDao.delete(order.toEntity())
}

@Singleton
class UserRepositoryImp @Inject constructor(private val userDao: UserDao) : UserRepository {
    override fun observeUser(id: Int): Flow<User?> = userDao.observarUsuario(id).map { it?.toDomain() }
    override fun authenticate(email: String, password: String): Flow<User?> =
        userDao.buscarPorCorreo(email.trim()).map { row ->
            row?.takeIf { PasswordHasher.verify(password, it.passwordHash) }?.toDomain()
        }
    override suspend fun register(user: User, password: String): Int =
        userDao.insertarUsuario(user.toEntity(PasswordHasher.hash(password))).toInt()
    override suspend fun update(user: User) {
        val current = userDao.observarUsuario(user.id).first()
            ?: throw IllegalArgumentException("No existe el usuario ${user.id}")
        userDao.actualizarUsuario(user.toEntity(current.passwordHash))
    }
    override suspend fun delete(user: User) {
        val current = userDao.observarUsuario(user.id).first()
            ?: throw IllegalArgumentException("No existe el usuario ${user.id}")
        userDao.eliminarUsuario(current)
    }
}

@Singleton
class CouponRepositoryImp @Inject constructor(private val couponDao: CouponDao) : CouponRepository {
    override fun observeActiveCoupon(code: String): Flow<Coupon?> =
        couponDao.findActive(code.trim().uppercase()).map { it?.toDomain() }
    override suspend fun save(coupon: Coupon) = couponDao.insert(coupon.toEntity())
    override suspend fun delete(coupon: Coupon) = couponDao.delete(coupon.toEntity())
}

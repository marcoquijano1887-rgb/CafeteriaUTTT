package mx.uttt.cafeuttt.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import mx.uttt.cafeuttt.data.database.entity.OrderStatus
import mx.uttt.cafeuttt.data.database.entity.PaymentMethod
import mx.uttt.cafeuttt.data.database.dao.CouponDao
import mx.uttt.cafeuttt.data.database.dao.OrderItemDao
import mx.uttt.cafeuttt.data.database.dao.ProteinOptionDao
import mx.uttt.cafeuttt.data.database.dao.CartDao
import mx.uttt.cafeuttt.data.database.dao.OrderDao
import mx.uttt.cafeuttt.data.database.dao.ProductDao
import mx.uttt.cafeuttt.data.database.dao.UserDao
import mx.uttt.cafeuttt.data.database.entity.*

class CafeConverters {
    @TypeConverter fun orderStatusToString(value: OrderStatus): String = value.name
    @TypeConverter fun stringToOrderStatus(value: String): OrderStatus = OrderStatus.valueOf(value)
    @TypeConverter fun paymentToString(value: PaymentMethod): String = value.name
    @TypeConverter fun stringToPayment(value: String): PaymentMethod = PaymentMethod.valueOf(value)
}

@Database(
    entities = [
        UserEntity::class, ProductEntity::class, ProteinOptionEntity::class, CouponEntity::class,
        CartItemEntity::class, OrderEntity::class, OrderItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(CafeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun proteinOptionDao(): ProteinOptionDao
    abstract fun couponDao(): CouponDao
    abstract fun orderItemDao(): OrderItemDao
}

package mx.uttt.cafeuttt.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class OrderStatus { RECIBIDO, PREPARANDO, LISTO, ENTREGADO, CANCELADO }
enum class PaymentMethod { EFECTIVO, TARJETA, SALDO_CREDENCIAL }

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    @ColumnInfo(name = "password_hash") val passwordHash: String,
    val balance: Int,
    @ColumnInfo(name = "balance_updated") val balanceUpdated: String
)

@Entity(tableName = "products", indices = [Index("category"), Index("name")])
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val price: Int,
    val category: String,
    val image: String? = null,
    val favorite: Boolean = false
)

@Entity(tableName = "protein_options", indices = [Index(value = ["name"], unique = true)])
data class ProteinOptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val extra: Int
)

@Entity(tableName = "coupons", indices = [Index(value = ["code"], unique = true)])
data class CouponEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String,
    val percent: Int,
    val active: Boolean = true
)

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(entity = ProductEntity::class, parentColumns = ["id"], childColumns = ["product_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ProteinOptionEntity::class, parentColumns = ["id"], childColumns = ["protein_option_id"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("product_id"), Index("protein_option_id")]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "product_id") val productId: Int,
    @ColumnInfo(name = "protein_option_id") val proteinOptionId: Int? = null,
    @ColumnInfo(name = "option_extra") val optionExtra: Int = 0,
    val notes: String = "",
    val qty: Int = 1
)

@Entity(
    tableName = "orders",
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["user_id"], onDelete = ForeignKey.RESTRICT)],
    indices = [Index("user_id"), Index("date")]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val number: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    val date: String,
    @ColumnInfo(name = "pickup_time") val pickupTime: String,
    @ColumnInfo(name = "payment_method") val paymentMethod: PaymentMethod,
    val subtotal: Int,
    val discount: Int,
    val total: Int,
    val status: OrderStatus = OrderStatus.RECIBIDO
)

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(entity = OrderEntity::class, parentColumns = ["number"], childColumns = ["order_number"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ProductEntity::class, parentColumns = ["id"], childColumns = ["product_id"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = ProteinOptionEntity::class, parentColumns = ["id"], childColumns = ["protein_option_id"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("order_number"), Index("product_id"), Index("protein_option_id")]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "order_number") val orderNumber: Int,
    @ColumnInfo(name = "product_id") val productId: Int,
    @ColumnInfo(name = "protein_option_id") val proteinOptionId: Int? = null,
    val notes: String = "",
    val qty: Int,
    @ColumnInfo(name = "unit_price") val unitPrice: Int
)

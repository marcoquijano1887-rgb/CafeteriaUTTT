package mx.uttt.cafeuttt.data.mapper

import mx.uttt.cafeuttt.data.database.dao.CartLine
import mx.uttt.cafeuttt.data.database.dao.OrderWithItems
import mx.uttt.cafeuttt.data.database.entity.*
import mx.uttt.cafeuttt.domain.model.*
import mx.uttt.cafeuttt.data.database.entity.OrderStatus as EntityOrderStatus
import mx.uttt.cafeuttt.data.database.entity.PaymentMethod as EntityPaymentMethod
import mx.uttt.cafeuttt.domain.model.OrderStatus as DomainOrderStatus
import mx.uttt.cafeuttt.domain.model.PaymentMethod as DomainPaymentMethod

fun ProductEntity.toDomain() = Product(id, name, description, price, category, image.orEmpty(), favorite)
fun Product.toEntity() = ProductEntity(id = id, name = name, description = description, price = price, category = category, image = emoji, favorite = favorite)
fun ProteinOptionEntity.toDomain() = ProteinOption(name = name, extra = extra, id = id)
fun ProteinOption.toEntity() = ProteinOptionEntity(id = id, name = name, extra = extra)
fun CouponEntity.toDomain() = Coupon(id, code, percent, active)
fun Coupon.toEntity() = CouponEntity(id = id, code = code, percent = percent, active = active)
fun UserEntity.toDomain() = User(name, email, balance, balanceUpdated, id)
fun User.toEntity(passwordHash: String) = UserEntity(id = id, name = name, email = email, passwordHash = passwordHash, balance = balance, balanceUpdated = balanceUpdated)

fun CartLine.toDomain() = CartItem(
    id = item.id.toLong(), product = product.toDomain(), option = protein?.name,
    optionExtra = item.optionExtra, notes = item.notes, qty = item.qty,
    proteinOptionId = item.proteinOptionId
)
fun CartItem.toEntity() = CartItemEntity(
    id = id.toInt(), productId = product.id, proteinOptionId = proteinOptionId,
    optionExtra = optionExtra, notes = notes, qty = qty
)

fun OrderEntity.toDomain() = Order(
    number = number, dateLabel = date, total = total,
    status = DomainOrderStatus.valueOf(status.name), pickupTime = pickupTime,
    userId = userId, date = date, paymentMethod = DomainPaymentMethod.valueOf(paymentMethod.name),
    subtotal = subtotal, discount = discount
)
fun Order.toEntity() = OrderEntity(
    number = number, userId = userId, date = date, pickupTime = pickupTime,
    paymentMethod = EntityPaymentMethod.valueOf(paymentMethod.name),
    subtotal = subtotal, discount = discount, total = total,
    status = EntityOrderStatus.valueOf(status.name)
)

fun OrderItemEntity.toDomain(product: ProductEntity) = OrderItem(
    id = id, orderNumber = orderNumber, product = product.toDomain(),
    proteinOptionId = proteinOptionId, notes = notes, qty = qty, unitPrice = unitPrice
)

fun OrderWithItems.toDomain() = order.toDomain()

package mx.uttt.cafeuttt.domain.model

data class Product(
    val id: Int, val name: String, val description: String, val price: Int,
    val category: String, val emoji: String, val favorite: Boolean = false
) {
    val hasProtein: Boolean get() = category == "Desayunos" || category == "Comidas"
}

data class ProteinOption(val name: String, val extra: Int, val id: Int = 0)

data class CartItem(
    val id: Long, val product: Product, val option: String? = null,
    val optionExtra: Int = 0, val notes: String = "", val qty: Int = 1,
    val proteinOptionId: Int? = null
) {
    val subtotal: Int get() = (product.price + optionExtra) * qty
}

enum class OrderStatus(val label: String) {
    RECIBIDO("Recibido"), PREPARANDO("Preparando"), LISTO("Listo"),
    ENTREGADO("Entregado"), CANCELADO("Cancelado")
}

enum class PaymentMethod { EFECTIVO, TARJETA, SALDO_CREDENCIAL }

data class Order(
    val number: Int, val dateLabel: String, val total: Int,
    val status: OrderStatus, val pickupTime: String = "",
    val userId: Int = 0, val date: String = dateLabel,
    val paymentMethod: PaymentMethod = PaymentMethod.EFECTIVO,
    val subtotal: Int = total, val discount: Int = 0,
    val items: List<OrderItem> = emptyList()
)

data class OrderItem(
    val id: Int = 0, val orderNumber: Int, val product: Product,
    val proteinOptionId: Int? = null, val notes: String = "",
    val qty: Int, val unitPrice: Int
)

data class User(
    val name: String, val email: String, val balance: Int,
    val balanceUpdated: String, val id: Int = 0
) {
    val initials: String get() = name.split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
}

data class Coupon(val id: Int = 0, val code: String, val percent: Int, val active: Boolean = true)

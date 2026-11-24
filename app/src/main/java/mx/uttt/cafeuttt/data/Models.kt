package mx.uttt.cafeuttt.data

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val category: String,
    val emoji: String,
    val favorite: Boolean = false
) {
    /** Solo desayunos y comidas permiten elegir proteína. */
    val hasProtein: Boolean get() = category == "Desayunos" || category == "Comidas"
}

data class ProteinOption(val name: String, val extra: Int)

data class CartItem(
    val id: Long,
    val product: Product,
    val option: String? = null,
    val optionExtra: Int = 0,
    val notes: String = "",
    val qty: Int = 1
) {
    val subtotal: Int get() = (product.price + optionExtra) * qty
}

enum class OrderStatus(val label: String) {
    RECIBIDO("Recibido"),
    PREPARANDO("Preparando"),
    LISTO("Listo"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado")
}

data class Order(
    val number: Int,
    val dateLabel: String,
    val total: Int,
    val status: OrderStatus,
    val pickupTime: String = ""
)

data class User(
    val name: String,
    val email: String,
    val balance: Int,
    val balanceUpdated: String
) {
    val initials: String
        get() = name.split(" ").filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

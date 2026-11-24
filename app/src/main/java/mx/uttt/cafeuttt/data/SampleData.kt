package mx.uttt.cafeuttt.data

/** Datos de ejemplo del prototipo. Más adelante se sustituyen por Room (SQLite). */
object SampleData {

    val categories = listOf("Todo", "Desayunos", "Comidas", "Bebidas", "Snacks")

    val proteins = listOf(
        ProteinOption("Pollo", 0),
        ProteinOption("Huevo", 5),
        ProteinOption("Sin proteína", -8)
    )

    val pickupTimes = listOf(
        "Hoy · 12:45", "Hoy · 13:00", "Hoy · 13:15", "Hoy · 13:30", "Hoy · 13:45"
    )

    val products = listOf(
        Product(
            1, "Chilaquiles verdes",
            "Totopos bañados en salsa verde, con pollo deshebrado, crema, queso y cebolla.",
            45, "Desayunos", "🍽️", favorite = true
        ),
        Product(2, "Café americano", "Café de grano recién molido, 8 oz.", 20, "Bebidas", "☕"),
        Product(3, "Torta de jamón", "Pan crujiente con jamón, queso y aguacate.", 38, "Comidas", "🥪"),
        Product(4, "Hot cakes", "Tres hot cakes esponjosos con miel y mantequilla.", 35, "Desayunos", "🥞"),
        Product(5, "Galletas de avena", "Galletas caseras de avena con pasas.", 15, "Snacks", "🍪")
    )

    fun productById(id: Int): Product? = products.firstOrNull { it.id == id }

    val user = User(
        name = "Andrea Martínez",
        email = "20240182@uttt.edu.mx",
        balance = 210,
        balanceUpdated = "hoy, 9:40"
    )

    val activeOrder = Order(124, "Hoy", 45, OrderStatus.PREPARANDO, "13:15")

    val history = listOf(
        Order(119, "14 oct", 58, OrderStatus.ENTREGADO),
        Order(107, "09 oct", 45, OrderStatus.ENTREGADO),
        Order(98, "02 oct", 20, OrderStatus.CANCELADO)
    )

    /** Cupón de ejemplo: 10 % de descuento. */
    const val COUPON_CODE = "UTTT10"
    const val COUPON_PERCENT = 10
}

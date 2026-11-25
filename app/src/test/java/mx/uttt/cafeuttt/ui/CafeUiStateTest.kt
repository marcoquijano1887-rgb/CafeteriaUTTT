package mx.uttt.cafeuttt.ui

import mx.uttt.cafeuttt.domain.model.CartItem
import mx.uttt.cafeuttt.domain.model.Coupon
import mx.uttt.cafeuttt.domain.model.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class CafeUiStateTest {
    @Test
    fun totalAplicaDiezPorCientoAlSubtotal() {
        val coffee = Product(1, "Café", "", 20, "Bebidas", "☕")
        val cakes = Product(2, "Hot cakes", "", 45, "Desayunos", "🥞")
        val state = CafeUiState(
            cart = listOf(CartItem(1, coffee, qty = 2), CartItem(2, cakes, qty = 2)),
            coupon = Coupon(code = "UTTT10", percent = 10)
        )

        assertEquals(130, state.subtotal)
        assertEquals(13, state.discount)
        assertEquals(117, state.total)
    }
}

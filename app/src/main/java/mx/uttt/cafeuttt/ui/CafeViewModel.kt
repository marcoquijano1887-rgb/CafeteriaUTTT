package mx.uttt.cafeuttt.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import mx.uttt.cafeuttt.data.CartItem
import mx.uttt.cafeuttt.data.Order
import mx.uttt.cafeuttt.data.OrderStatus
import mx.uttt.cafeuttt.data.Product
import mx.uttt.cafeuttt.data.SampleData

data class CafeUiState(
    val cart: List<CartItem> = emptyList(),
    val history: List<Order> = SampleData.history,
    val activeOrder: Order? = SampleData.activeOrder,
    val couponCode: String? = null,
    val couponError: String? = null,
    val message: String? = null
) {
    val cartCount: Int get() = cart.sumOf { it.qty }
    val subtotal: Int get() = cart.sumOf { it.subtotal }
    val discount: Int
        get() = if (couponCode != null) subtotal * SampleData.COUPON_PERCENT / 100 else 0
    val total: Int get() = subtotal - discount
}

/** Estado temporal de la interfaz, expuesto como flujo para mantener UDF. */
class CafeViewModel : ViewModel() {
    val user = SampleData.user

    private val _uiState = MutableStateFlow(CafeUiState())
    val uiState: StateFlow<CafeUiState> = _uiState.asStateFlow()

    private var nextItemId = 1L
    private var lastOrderNumber = SampleData.activeOrder.number

    fun addQuick(product: Product) {
        _uiState.update { state ->
            val itemIndex = state.cart.indexOfFirst {
                it.product.id == product.id && it.option == null && it.notes.isBlank()
            }
            val updatedCart = if (itemIndex >= 0) {
                state.cart.mapIndexed { index, item ->
                    if (index == itemIndex) item.copy(qty = item.qty + 1) else item
                }
            } else {
                state.cart + CartItem(nextItemId++, product)
            }
            state.copy(cart = updatedCart, message = "${product.name} agregado al carrito")
        }
    }

    fun addToCart(product: Product, option: String?, optionExtra: Int, notes: String, qty: Int) {
        _uiState.update { state ->
            state.copy(
                cart = state.cart + CartItem(nextItemId++, product, option, optionExtra, notes.trim(), qty),
                message = "${product.name} agregado al carrito"
            )
        }
    }

    fun changeQty(itemId: Long, delta: Int) {
        _uiState.update { state ->
            state.copy(
                cart = state.cart.map { item ->
                    if (item.id == itemId) item.copy(qty = (item.qty + delta).coerceAtLeast(1)) else item
                }
            )
        }
    }

    fun remove(itemId: Long) {
        _uiState.update { state ->
            val updatedCart = state.cart.filterNot { it.id == itemId }
            state.copy(
                cart = updatedCart,
                couponCode = if (updatedCart.isEmpty()) null else state.couponCode,
                couponError = if (updatedCart.isEmpty()) null else state.couponError
            )
        }
    }

    fun applyCoupon(code: String) {
        if (code.trim().equals(SampleData.COUPON_CODE, ignoreCase = true)) {
            _uiState.update { it.copy(couponCode = SampleData.COUPON_CODE, couponError = null) }
        } else {
            _uiState.update { it.copy(couponCode = null, couponError = "Cupón no válido") }
        }
    }

    fun clearCouponError() {
        _uiState.update { it.copy(couponError = null) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun confirmOrder(pickupTime: String) {
        _uiState.update { state ->
            if (state.cart.isEmpty()) return@update state

            val updatedHistory = state.activeOrder
                ?.let { listOf(it.copy(status = OrderStatus.ENTREGADO)) + state.history }
                ?: state.history
            lastOrderNumber += 1
            val confirmedOrder = Order(
                number = lastOrderNumber,
                dateLabel = "Hoy",
                total = state.total,
                status = OrderStatus.RECIBIDO,
                pickupTime = pickupTime.substringAfter("· ").trim()
            )

            state.copy(
                cart = emptyList(),
                history = updatedHistory,
                activeOrder = confirmedOrder,
                couponCode = null,
                couponError = null,
                message = "Tu pedido fue confirmado"
            )
        }
    }
}

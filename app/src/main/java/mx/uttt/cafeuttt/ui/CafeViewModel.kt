package mx.uttt.cafeuttt.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mx.uttt.cafeuttt.domain.model.*
import mx.uttt.cafeuttt.domain.repository.*

data class CafeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val loginError: String? = null,
    val isSigningIn: Boolean = false,
    val products: List<Product> = emptyList(),
    val categories: List<String> = listOf("Todo", "Desayunos", "Comidas", "Bebidas", "Snacks"),
    val query: String = "",
    val category: String = "Todo",
    val detailProduct: Product? = null,
    val proteins: List<ProteinOption> = emptyList(),
    val selectedProteinId: Int? = null,
    val cart: List<CartItem> = emptyList(),
    val couponInput: String = "",
    val coupon: Coupon? = null,
    val couponError: String? = null,
    val user: User? = null,
    val orders: List<Order> = emptyList(),
    val activeOrder: Order? = null,
    val message: String? = null,
    val confirmationEvent: Int = 0,
    val isConfirming: Boolean = false
) {
    val cartCount: Int get() = cart.sumOf { it.qty }
    val subtotal: Int get() = cart.sumOf { it.subtotal }
    val discount: Int get() = coupon?.let { subtotal * it.percent / 100 } ?: 0
    val total: Int get() = (subtotal - discount).coerceAtLeast(0)
    val history: List<Order> get() = orders.filter { it.status == OrderStatus.ENTREGADO || it.status == OrderStatus.CANCELADO }
}

/** ViewModel UDF compartido: las pantallas reciben UiState y emiten eventos. */
@HiltViewModel
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class CafeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val couponRepository: CouponRepository,
    private val session: UserSession
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val category = MutableStateFlow("Todo")
    private val detailId = MutableStateFlow<Int?>(null)
    private val proteinId = MutableStateFlow<Int?>(null)
    private val coupon = MutableStateFlow<Coupon?>(null)
    private val couponInput = MutableStateFlow("")
    private val error = MutableStateFlow<String?>(null)
    private val loginError = MutableStateFlow<String?>(null)
    private val signingIn = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)
    private val confirming = MutableStateFlow(false)
    private val confirmationEvent = MutableStateFlow(0)

    private val products = combine(category, query) { selectedCategory, search ->
        selectedCategory.takeUnless { it == "Todo" } to search
    }.flatMapLatest { (selectedCategory, search) ->
        productRepository.observeProducts(selectedCategory, search)
    }
    private val detailProduct = detailId.flatMapLatest { id ->
        if (id == null) flowOf(null) else productRepository.observeProduct(id)
    }
    private val currentUser = session.user.flatMapLatest { user ->
        if (user == null) flowOf(null) else userRepository.observeUser(user.id)
    }
    private val orders = session.user.flatMapLatest { user ->
        if (user == null) flowOf(emptyList()) else orderRepository.observeOrders(user.id)
    }
    private val activeOrder = session.user.flatMapLatest { user ->
        if (user == null) flowOf(null) else orderRepository.observeActiveOrder(user.id)
    }

    private data class Observed(
        val products: List<Product>, val proteins: List<ProteinOption>, val cart: List<CartItem>,
        val user: User?, val orders: List<Order>, val activeOrder: Order? = null,
        val detailProduct: Product? = null
    )
    private data class SearchControls(val query: String, val category: String, val proteinId: Int?, val couponInput: String)
    private data class Controls(
        val search: SearchControls, val coupon: Coupon?, val error: String?, val loginError: String?,
        val signingIn: Boolean, val message: String?, val confirming: Boolean, val confirmationEvent: Int
    )

    private val baseObserved = combine(
        products, productRepository.observeProteinOptions(), cartRepository.observeCart(), currentUser, orders
    ) { menu, proteins, cart, user, orderList -> Observed(menu, proteins, cart, user, orderList) }
    private val observed = combine(baseObserved, activeOrder, detailProduct) { base, active, detail ->
        base.copy(activeOrder = active, detailProduct = detail)
    }
    private val searchControls = combine(query, category, proteinId, couponInput) { q, c, protein, input ->
        SearchControls(q, c, protein, input)
    }
    private val basicControls = combine(searchControls, coupon, error, loginError, signingIn) { search, applied, errorText, authError, busy ->
        arrayOf(search, applied, errorText, authError, busy)
    }
    private val controls = combine(basicControls, message, confirming, confirmationEvent) { basic, notice, busy, confirmation ->
        Controls(
            search = basic[0] as SearchControls, coupon = basic[1] as Coupon?,
            error = basic[2] as String?, loginError = basic[3] as String?, signingIn = basic[4] as Boolean,
            message = notice, confirming = busy, confirmationEvent = confirmation
        )
    }

    val uiState: StateFlow<CafeUiState> = combine(observed, controls) { data, c ->
        CafeUiState(
            isLoading = false, error = c.error, loginError = c.loginError,
            isSigningIn = c.signingIn, products = data.products, query = c.search.query,
            category = c.search.category, detailProduct = data.detailProduct, proteins = data.proteins,
            selectedProteinId = c.search.proteinId, cart = data.cart, couponInput = c.search.couponInput,
            coupon = c.coupon, user = data.user, orders = data.orders,
            activeOrder = data.activeOrder, message = c.message,
            isConfirming = c.confirming, confirmationEvent = c.confirmationEvent
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CafeUiState())

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            loginError.value = null
            if (email.isBlank() || password.isBlank()) { loginError.value = "Ingresa correo y contraseña"; return@launch }
            signingIn.value = true
            try {
                val user = userRepository.authenticate(email.trim(), password).first()
                if (user == null) loginError.value = "Correo o contraseña incorrectos"
                else { session.setUser(user); loginError.value = null }
            } catch (e: Exception) { loginError.value = e.message ?: "No se pudo iniciar sesión" }
            finally { signingIn.value = false }
        }
    }

    fun setQuery(value: String) { query.value = value }
    fun setCategory(value: String) { category.value = value }
    fun openProduct(id: Int) {
        if (detailId.value != id) {
            detailId.value = id
            proteinId.value = null
        }
    }
    fun closeProduct() { detailId.value = null; proteinId.value = null }
    fun selectProtein(id: Int) { proteinId.value = id }
    fun addQuick(product: Product) {
        viewModelScope.launch {
            try {
                cartRepository.add(CartItem(0, product))
                message.value = "${product.name} agregado al carrito"
            } catch (e: Exception) { error.value = e.message ?: "No se pudo agregar el producto" }
        }
    }
    fun addDetailToCart(notes: String, qty: Int) {
        val state = uiState.value
        val product = state.detailProduct ?: return
        val protein = state.proteins.firstOrNull { it.id == (state.selectedProteinId ?: state.proteins.firstOrNull()?.id) }
        viewModelScope.launch {
            try {
                cartRepository.add(CartItem(0, product, protein?.name, protein?.extra ?: 0, notes.trim(), qty, protein?.id))
                message.value = "${product.name} agregado al carrito"
            } catch (e: Exception) { error.value = e.message ?: "No se pudo agregar al carrito" }
        }
    }
    fun changeQty(item: CartItem, delta: Int) {
        viewModelScope.launch {
            val qty = item.qty + delta
            if (qty <= 0) cartRepository.remove(item) else cartRepository.update(item.copy(qty = qty))
        }
    }
    fun remove(item: CartItem) { viewModelScope.launch { cartRepository.remove(item) } }
    fun setCouponInput(value: String) { couponInput.value = value; error.value = null }
    fun applyCoupon() {
        viewModelScope.launch {
            coupon.value = null
            try {
                val found = couponRepository.observeActiveCoupon(couponInput.value.trim()).first()
                if (found == null) error.value = "Cupón no válido" else { coupon.value = found; error.value = null }
            } catch (e: Exception) { error.value = e.message ?: "No se pudo validar el cupón" }
        }
    }
    fun clearMessage() { message.value = null }
    fun clearError() { error.value = null }
    fun clearCoupon() { coupon.value = null; couponInput.value = ""; error.value = null }

    fun confirmOrder(pickupTime: String, paymentMethod: PaymentMethod) {
        val user = uiState.value.user ?: run { error.value = "Inicia sesión para confirmar"; return }
        viewModelScope.launch {
            confirming.value = true
            try {
                val now = Date()
                val locale = Locale.forLanguageTag("es-MX")
                orderRepository.confirmOrder(
                    userId = user.id,
                    date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", locale).format(now),
                    pickupTime = pickupTime.substringAfter("·").trim(),
                    paymentMethod = paymentMethod,
                    discountPercent = coupon.value?.percent ?: 0,
                    balanceUpdated = SimpleDateFormat("dd/MM/yyyy HH:mm", locale).format(now)
                )
                clearCoupon()
                message.value = "Tu pedido fue confirmado"
                confirmationEvent.value += 1
            } catch (e: Exception) { error.value = e.message ?: "No se pudo confirmar el pedido" }
            finally { confirming.value = false }
        }
    }
    fun consumeConfirmation() { confirmationEvent.value = 0 }
}

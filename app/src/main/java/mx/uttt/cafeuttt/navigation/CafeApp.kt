package mx.uttt.cafeuttt.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.uttt.cafeuttt.ui.CafeViewModel
import mx.uttt.cafeuttt.ui.screens.CartScreen
import mx.uttt.cafeuttt.ui.screens.ConfirmOrderScreen
import mx.uttt.cafeuttt.ui.screens.LoginScreen
import mx.uttt.cafeuttt.ui.screens.MenuScreen
import mx.uttt.cafeuttt.ui.screens.OrdersScreen
import mx.uttt.cafeuttt.ui.screens.ProductDetailScreen
import mx.uttt.cafeuttt.ui.screens.ProfileScreen

object Routes {
    const val LOGIN = "login"
    const val MENU = "menu"
    const val DETAIL = "detail/{id}"
    const val CART = "cart"
    const val CONFIRM = "confirm"
    const val ORDERS = "orders"
    const val PROFILE = "profile"
    fun detail(id: Int) = "detail/$id"
}

@Composable
fun CafeApp(vm: CafeViewModel = hiltViewModel()) {
    val nav = rememberNavController()
    val state by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.user?.id) {
        if (state.user != null && nav.currentDestination?.route == Routes.LOGIN) {
            nav.navigate(Routes.MENU) { popUpTo(Routes.LOGIN) { inclusive = true } }
        }
    }
    LaunchedEffect(state.confirmationEvent) {
        if (state.confirmationEvent > 0) {
            nav.navigate(Routes.ORDERS) { popUpTo(Routes.MENU) }
            vm.consumeConfirmation()
        }
    }

    // Navegación de la barra inferior: conserva el estado de cada sección.
    val goTab: (String) -> Unit = { route ->
        nav.navigate(route) {
            popUpTo(Routes.MENU) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(navController = nav, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(state = state, onLogin = vm::signIn)
        }

        composable(Routes.MENU) {
            MenuScreen(
                state = state,
                onQueryChange = vm::setQuery,
                onCategoryChange = vm::setCategory,
                onAdd = vm::addQuick,
                onClearMessage = vm::clearMessage,
                onProduct = { nav.navigate(Routes.detail(it)) },
                onCart = { goTab(Routes.CART) },
                onNavigate = goTab
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val productId = entry.arguments?.getInt("id") ?: 0
            LaunchedEffect(productId) { vm.openProduct(productId) }
            ProductDetailScreen(
                state = state,
                productId = productId,
                onSelectProtein = vm::selectProtein,
                onAdd = vm::addDetailToCart,
                onBack = { vm.closeProduct(); nav.popBackStack() }
            )
        }

        composable(Routes.CART) {
            CartScreen(
                state = state,
                onQtyChange = vm::changeQty,
                onRemove = vm::remove,
                onCouponInput = vm::setCouponInput,
                onApplyCoupon = vm::applyCoupon,
                onClearError = vm::clearError,
                onBack = { nav.popBackStack() },
                onCheckout = { nav.navigate(Routes.CONFIRM) },
                onGoMenu = { goTab(Routes.MENU) },
                onNavigate = goTab
            )
        }

        composable(Routes.CONFIRM) {
            ConfirmOrderScreen(
                state = state,
                onBack = { nav.popBackStack() },
                onConfirmed = vm::confirmOrder
            )
        }

        composable(Routes.ORDERS) {
            OrdersScreen(state = state, onClearMessage = vm::clearMessage, onNavigate = goTab)
        }

        composable(Routes.PROFILE) {
            ProfileScreen(state = state, onNavigate = goTab)
        }
    }
}

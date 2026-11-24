package mx.uttt.cafeuttt.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
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
fun CafeApp(vm: CafeViewModel = viewModel()) {
    val nav = rememberNavController()

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
            LoginScreen(onLogin = {
                nav.navigate(Routes.MENU) { popUpTo(Routes.LOGIN) { inclusive = true } }
            })
        }

        composable(Routes.MENU) {
            MenuScreen(
                vm = vm,
                onProduct = { nav.navigate(Routes.detail(it)) },
                onCart = { goTab(Routes.CART) },
                onNavigate = goTab
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            ProductDetailScreen(
                vm = vm,
                productId = entry.arguments?.getInt("id") ?: 0,
                onBack = { nav.popBackStack() }
            )
        }

        composable(Routes.CART) {
            CartScreen(
                vm = vm,
                onBack = { nav.popBackStack() },
                onCheckout = { nav.navigate(Routes.CONFIRM) },
                onGoMenu = { goTab(Routes.MENU) },
                onNavigate = goTab
            )
        }

        composable(Routes.CONFIRM) {
            ConfirmOrderScreen(
                vm = vm,
                onBack = { nav.popBackStack() },
                onConfirmed = {
                    nav.navigate(Routes.ORDERS) { popUpTo(Routes.MENU) }
                }
            )
        }

        composable(Routes.ORDERS) {
            OrdersScreen(vm = vm, onNavigate = goTab)
        }

        composable(Routes.PROFILE) {
            ProfileScreen(vm = vm, onNavigate = goTab)
        }
    }
}

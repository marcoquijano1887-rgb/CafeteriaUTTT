package mx.uttt.cafeuttt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.uttt.cafeuttt.navigation.Routes

fun money(value: Int): String = if (value < 0) "-\$${-value}" else "\$$value"

/** Barra superior terracota usada en todas las pantallas. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actionText: String? = null,
    onAction: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                }
            }
        },
        actions = {
            if (actionText != null) {
                TextButton(onClick = onAction) {
                    Text(
                        actionText,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

private data class Tab(val label: String, val route: String, val icon: ImageVector)

private val tabs = listOf(
    Tab("Inicio", Routes.MENU, Icons.Outlined.Home),
    Tab("Pedidos", Routes.ORDERS, Icons.Outlined.Receipt),
    Tab("Carrito", Routes.CART, Icons.Outlined.ShoppingCart),
    Tab("Perfil", Routes.PROFILE, Icons.Outlined.Person)
)

/** Barra de navegación inferior de 4 ítems (Inicio, Pedidos, Carrito, Perfil). */
@Composable
fun CafeBottomBar(current: String, cartCount: Int, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = current == tab.route,
                onClick = { onNavigate(tab.route) },
                icon = {
                    if (tab.route == Routes.CART && cartCount > 0) {
                        BadgedBox(badge = { Badge { Text("$cartCount") } }) {
                            Icon(tab.icon, contentDescription = tab.label)
                        }
                    } else {
                        Icon(tab.icon, contentDescription = tab.label)
                    }
                },
                label = { Text(tab.label, fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

/** Imagen del producto (emoji sobre fondo durazno, sustituible por una imagen real). */
@Composable
fun ProductImage(emoji: String, size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = (size.value * 0.5f).sp)
    }
}

/** Selector numérico con botones − y +. */
@Composable
fun QuantitySelector(qty: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedIconButton(onClick = onMinus, modifier = Modifier.size(48.dp)) {
            Icon(Icons.Filled.Remove, contentDescription = "Disminuir", modifier = Modifier.size(16.dp))
        }
        Text(
            "$qty",
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        FilledIconButton(onClick = onPlus, modifier = Modifier.size(48.dp)) {
            Icon(Icons.Filled.Add, contentDescription = "Aumentar", modifier = Modifier.size(16.dp))
        }
    }
}

/** Fila con botón de radio, etiqueta y texto opcional a la derecha. */
@Composable
fun RadioRow(
    selected: Boolean,
    label: String,
    trailing: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled
        )
        Text(
            label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        if (trailing != null) {
            Text(
                trailing,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier = modifier,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

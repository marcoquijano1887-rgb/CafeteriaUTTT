package mx.uttt.cafeuttt.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.uttt.cafeuttt.data.CartItem
import mx.uttt.cafeuttt.ui.CafeViewModel
import mx.uttt.cafeuttt.ui.components.CafeTopBar
import mx.uttt.cafeuttt.ui.components.ProductImage
import mx.uttt.cafeuttt.ui.components.QuantitySelector
import mx.uttt.cafeuttt.ui.components.SectionLabel
import mx.uttt.cafeuttt.ui.components.money

/** Pantalla 4. Carrito. */
@Composable
fun CartScreen(
    vm: CafeViewModel,
    onBack: () -> Unit,
    onCheckout: () -> Unit,
    onGoMenu: () -> Unit
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var code by rememberSaveable { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { CafeTopBar("Mi carrito", onBack = onBack) },
        bottomBar = {
            if (uiState.cart.isNotEmpty()) {
                Surface(color = MaterialTheme.colorScheme.background, shadowElevation = 8.dp) {
                    Button(
                        onClick = onCheckout,
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Continuar al pago", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { padding ->
        if (uiState.cart.isEmpty()) {
            // Carrito vacío
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🛒", fontSize = 56.sp)
                Spacer(Modifier.height(12.dp))
                Text("Tu carrito está vacío", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Regresa al menú para elegir algo rico.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = onGoMenu, shape = RoundedCornerShape(14.dp)) {
                    Text("Ver menú", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.cart, key = { it.id }) { item ->
                    CartItemCard(
                        item = item,
                        onMinus = { vm.changeQty(item.id, -1) },
                        onPlus = { vm.changeQty(item.id, +1) },
                        onRemove = { vm.remove(item.id) }
                    )
                }

                item {
                    Column {
                        SectionLabel("Cupón de descuento")
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            OutlinedTextField(
                                value = code,
                                onValueChange = { code = it; vm.clearCouponError() },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Código") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                isError = uiState.couponError != null,
                                supportingText = {
                                    val error = uiState.couponError
                                    if (error != null) {
                                        Text(error)
                                    } else if (uiState.couponCode != null) {
                                        Text("Cupón aplicado")
                                    }
                                }
                            )
                            Spacer(Modifier.padding(4.dp))
                            OutlinedButton(
                                onClick = { vm.applyCoupon(code) },
                                modifier = Modifier.height(56.dp),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Aplicar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        HorizontalDivider()
                        AmountRow("Subtotal", money(uiState.subtotal))
                        AmountRow("Descuento", money(-uiState.discount))
                        AmountRow("Total", money(uiState.total), bold = true)
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(item: CartItem, onMinus: () -> Unit, onPlus: () -> Unit, onRemove: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            ProductImage(item.product.emoji, 64.dp)
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(item.product.name, fontWeight = FontWeight.Bold)
                Text(
                    item.option ?: item.product.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (item.notes.isNotBlank()) {
                    Text(
                        "Nota: ${item.notes}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(6.dp))
                QuantitySelector(item.qty, onMinus, onPlus)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(money(item.subtotal), fontWeight = FontWeight.ExtraBold)
                TextButton(onClick = onRemove, contentPadding = PaddingValues(horizontal = 4.dp)) {
                    Text("Quitar", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun AmountRow(label: String, value: String, bold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            label,
            modifier = Modifier.weight(1f),
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.Normal
        )
        Text(
            value,
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.Normal
        )
    }
}

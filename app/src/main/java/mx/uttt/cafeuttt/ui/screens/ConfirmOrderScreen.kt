package mx.uttt.cafeuttt.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.uttt.cafeuttt.data.SampleData
import mx.uttt.cafeuttt.ui.CafeViewModel
import mx.uttt.cafeuttt.ui.components.CafeTopBar
import mx.uttt.cafeuttt.ui.components.RadioRow
import mx.uttt.cafeuttt.ui.components.SectionLabel
import mx.uttt.cafeuttt.ui.components.money

private val paymentMethods = listOf("Efectivo en caja", "Tarjeta de débito/crédito", "Saldo de credencial")

/** Pantalla 5. Confirmar pedido. */
@Composable
fun ConfirmOrderScreen(vm: CafeViewModel, onBack: () -> Unit, onConfirmed: () -> Unit) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var timeIndex by rememberSaveable { mutableIntStateOf(2) }
    var payment by rememberSaveable { mutableIntStateOf(0) }
    var menuOpen by remember { mutableStateOf(false) }

    val balance = vm.user.balance
    val canPayWithBalance = balance >= uiState.total

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { CafeTopBar("Confirmar pedido", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Hora de recogida (lista desplegable)
            SectionLabel("Hora de recogida")
            Spacer(Modifier.height(6.dp))
            Box {
                OutlinedCard(
                    onClick = { menuOpen = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            SampleData.pickupTimes[timeIndex],
                            modifier = Modifier.weight(1f).padding(start = 10.dp),
                            fontWeight = FontWeight.Medium
                        )
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Abrir horarios")
                    }
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    SampleData.pickupTimes.forEachIndexed { i, t ->
                        DropdownMenuItem(
                            text = { Text(t) },
                            onClick = { timeIndex = i; menuOpen = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Método de pago
            SectionLabel("Método de pago")
            paymentMethods.forEachIndexed { i, label ->
                val isBalance = i == 2
                RadioRow(
                    selected = payment == i,
                    label = label,
                    trailing = if (isBalance) money(balance) else null,
                    enabled = !isBalance || canPayWithBalance,
                    onClick = { payment = i }
                )
            }
            if (!canPayWithBalance) {
                Text(
                    "Tu saldo no alcanza para este pedido.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(16.dp))

            // Resumen
            SectionLabel("Resumen")
            Spacer(Modifier.height(6.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    uiState.cart.forEach { item ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                            Text(
                                "${item.qty} × ${item.product.name}",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(money(item.subtotal), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    if (uiState.discount > 0) {
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                            Text("Descuento", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                            Text(money(-uiState.discount), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Text("Total a pagar", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        Text(money(uiState.total), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Aviso de recogida
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    Column(Modifier.padding(start = 12.dp)) {
                        Text("Recoge en caja 2", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Te pediremos el código de tu pedido.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    vm.confirmOrder(SampleData.pickupTimes[timeIndex])
                    onConfirmed()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Confirmar pedido", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Cancelar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

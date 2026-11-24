package mx.uttt.cafeuttt.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.uttt.cafeuttt.data.Order
import mx.uttt.cafeuttt.data.OrderStatus
import mx.uttt.cafeuttt.navigation.Routes
import mx.uttt.cafeuttt.ui.CafeViewModel
import mx.uttt.cafeuttt.ui.components.CafeBottomBar
import mx.uttt.cafeuttt.ui.components.CafeTopBar
import mx.uttt.cafeuttt.ui.components.money
import mx.uttt.cafeuttt.ui.theme.Tinta
import mx.uttt.cafeuttt.ui.theme.VerdeAvance
import mx.uttt.cafeuttt.ui.theme.VerdeSuave
import java.util.Random

/** Pantalla 6. Mis pedidos. */
@Composable
fun OrdersScreen(vm: CafeViewModel, onNavigate: (String) -> Unit) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbar.showSnackbar(it)
            vm.clearMessage()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { CafeTopBar("Mis pedidos") },
        bottomBar = { CafeBottomBar(Routes.ORDERS, uiState.cartCount, onNavigate) },
        snackbarHost = {
            // Snackbar verde de confirmación, como en el prototipo
            SnackbarHost(snackbar) { data ->
                Snackbar(
                    containerColor = VerdeSuave,
                    contentColor = VerdeAvance,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = VerdeAvance)
                        Text(
                            data.visuals.message,
                            modifier = Modifier.padding(start = 10.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            uiState.activeOrder?.let { order ->
                item { ActiveOrderCard(order) }
            }
            item {
                Text(
                    "Historial",
                    modifier = Modifier.padding(top = 6.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            items(uiState.history, key = { it.number }) { order -> HistoryCard(order) }
        }
    }
}

@Composable
private fun ActiveOrderCard(order: Order) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    "PEDIDO ACTIVO",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Total ${money(order.total)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "Pedido #%04d".format(order.number),
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            OrderStepper(stage = order.status.ordinal.coerceAtMost(2))

            Row(
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Schedule, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(
                    "Recogida estimada: ${order.pickupTime}",
                    modifier = Modifier.padding(start = 6.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(12.dp))
            QrPlaceholder(seed = order.number.toLong(), modifier = Modifier.size(150.dp))
            Spacer(Modifier.height(6.dp))
            Text(
                "Muestra este código en caja",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Indicador de progreso de tres etapas: Recibido, Preparando, Listo. */
@Composable
private fun OrderStepper(stage: Int) {
    val steps = listOf(OrderStatus.RECIBIDO, OrderStatus.PREPARANDO, OrderStatus.LISTO)
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { i, _ ->
                val done = i <= stage
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (done) VerdeAvance else Color.White)
                        .border(2.dp, if (done) VerdeAvance else MaterialTheme.colorScheme.outline, CircleShape)
                )
                if (i < steps.lastIndex) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(if (i < stage) VerdeAvance else MaterialTheme.colorScheme.outline)
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            steps.forEachIndexed { i, s ->
                Text(
                    s.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (i <= stage) FontWeight.Bold else FontWeight.Normal,
                    color = if (i <= stage) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Cuadro tipo QR generado a partir del número de pedido (solo visual).
 * Para un QR real se puede usar la biblioteca ZXing (com.google.zxing:core).
 */
@Composable
private fun QrPlaceholder(seed: Long, modifier: Modifier = Modifier) {
    val grid = 21
    val cells = remember(seed) {
        val rnd = Random(seed * 7919)
        Array(grid) { BooleanArray(grid) { rnd.nextInt(100) < 48 } }
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(8.dp)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val cell = size.width / grid
            fun finder(cx: Int, cy: Int) {
                // Marcador de esquina: cuadro oscuro 7x7 con centro claro
                drawRect(Tinta, Offset(cx * cell, cy * cell), Size(7 * cell, 7 * cell))
                drawRect(Color.White, Offset((cx + 1) * cell, (cy + 1) * cell), Size(5 * cell, 5 * cell))
                drawRect(Tinta, Offset((cx + 2) * cell, (cy + 2) * cell), Size(3 * cell, 3 * cell))
            }
            for (y in 0 until grid) for (x in 0 until grid) {
                val inFinder = (x < 8 && y < 8) || (x >= grid - 8 && y < 8) || (x < 8 && y >= grid - 8)
                if (!inFinder && cells[y][x]) {
                    drawRect(Tinta, Offset(x * cell, y * cell), Size(cell, cell))
                }
            }
            finder(0, 0); finder(grid - 7, 0); finder(0, grid - 7)
        }
    }
}

@Composable
private fun HistoryCard(order: Order) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("#%04d · %s".format(order.number, order.dateLabel), fontWeight = FontWeight.Bold)
                Text(
                    order.status.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (order.status == OrderStatus.CANCELADO) MaterialTheme.colorScheme.error else VerdeAvance
                )
            }
            Text(money(order.total), fontWeight = FontWeight.ExtraBold)
        }
    }
}

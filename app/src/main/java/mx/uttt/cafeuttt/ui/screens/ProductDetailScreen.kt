package mx.uttt.cafeuttt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import mx.uttt.cafeuttt.data.SampleData
import mx.uttt.cafeuttt.ui.CafeViewModel
import mx.uttt.cafeuttt.ui.components.CafeTopBar
import mx.uttt.cafeuttt.ui.components.QuantitySelector
import mx.uttt.cafeuttt.ui.components.RadioRow
import mx.uttt.cafeuttt.ui.components.SectionLabel
import mx.uttt.cafeuttt.ui.components.money

/** Pantalla 3. Detalle del producto. */
@Composable
fun ProductDetailScreen(vm: CafeViewModel, productId: Int, onBack: () -> Unit) {
    val product = SampleData.productById(productId)
    if (product == null) return

    var proteinIndex by rememberSaveable { mutableIntStateOf(0) }
    var notes by rememberSaveable { mutableStateOf("") }
    var qty by rememberSaveable { mutableIntStateOf(1) }

    val protein = if (product.hasProtein) SampleData.proteins[proteinIndex] else null
    val unitPrice = product.price + (protein?.extra ?: 0)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { CafeTopBar("Detalle", onBack = onBack) },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.background, shadowElevation = 8.dp) {
                Button(
                    onClick = {
                        vm.addToCart(product, protein?.name, protein?.extra ?: 0, notes, qty)
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Agregar al carrito · ${money(unitPrice * qty)}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Imagen grande con etiqueta
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(product.emoji, fontSize = 96.sp)
                if (product.favorite) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            "FAVORITO DEL CAMPUS",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                product.category.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    product.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    money(product.price),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            if (product.hasProtein) {
                SectionLabel("Elige tu proteína")
                SampleData.proteins.forEachIndexed { i, option ->
                    RadioRow(
                        selected = proteinIndex == i,
                        label = option.name,
                        trailing = when {
                            option.extra > 0 -> "+${money(option.extra)}"
                            option.extra < 0 -> money(option.extra)
                            else -> "Incluido"
                        },
                        onClick = { proteinIndex = i }
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            SectionLabel("Notas para la cocina")
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej. sin cebolla, salsa aparte") },
                shape = RoundedCornerShape(14.dp),
                minLines = 2
            )

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionLabel("Cantidad", Modifier.weight(1f))
                QuantitySelector(
                    qty = qty,
                    onMinus = { if (qty > 1) qty-- },
                    onPlus = { qty++ }
                )
            }
        }
    }
}

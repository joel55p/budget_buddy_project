package com.uvg.budget_buddy.ui.features.transactionDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uvg.budget_buddy.ui.features.home.DashboardViewModel
import com.uvg.budget_buddy.ui.theme.SoftGreen
import com.uvg.budget_buddy.ui.theme.SoftRed
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: Long,
    dashboardVm: DashboardViewModel,
    onBackClick: () -> Unit
) {
    val state by dashboardVm.state.collectAsStateWithLifecycle()
    val transaction = state.transactions.find { it.id == transactionId }
    val df = remember { DecimalFormat("#,##0.00") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Transacción") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (transaction == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Transacción no encontrada",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onBackClick) {
                        Text("Volver")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono tipo transacción
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            if (transaction.amount >= 0) SoftGreen.copy(alpha = 0.2f)
                            else SoftRed.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (transaction.amount >= 0) Icons.Default.TrendingUp
                        else Icons.Default.TrendingDown,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = if (transaction.amount >= 0) SoftGreen else SoftRed
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Monto
                Text(
                    text = if (transaction.amount >= 0) "Ingreso" else "Gasto",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                val amountAbs = kotlin.math.abs(transaction.amount)
                Text(
                    text = "Q ${df.format(amountAbs)}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.amount >= 0) SoftGreen else SoftRed
                )

                Spacer(Modifier.height(32.dp))

                // Detalles en Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        DetailRow(
                            icon = Icons.Default.Description,
                            label = "Descripción",
                            value = transaction.description
                        )
                        Divider(Modifier.padding(vertical = 12.dp))
                        DetailRow(
                            icon = Icons.Default.CalendarToday,
                            label = "Fecha",
                            value = transaction.dateText
                        )
                        Divider(Modifier.padding(vertical = 12.dp))
                        DetailRow(
                            icon = Icons.Default.Tag,
                            label = "ID",
                            value = "#${transaction.id}"
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al Dashboard")
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
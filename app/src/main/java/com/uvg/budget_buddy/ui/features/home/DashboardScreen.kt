package com.uvg.budget_buddy.ui.features.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uvg.budget_buddy.ui.theme.SoftBlue
import com.uvg.budget_buddy.ui.theme.SoftGreen
import com.uvg.budget_buddy.ui.theme.SoftRed
import kotlinx.coroutines.flow.StateFlow
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max

data class FinancialData(val label: String, val amount: String, val color: androidx.compose.ui.graphics.Color)
data class MonthlyPoint1(val month: String, val value: Float)

@Composable
fun DashboardScreen(
    stateFlow: StateFlow<DashboardUiState>,
    onAddIncomeClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onOpenTxDetail: (Long) -> Unit
) {
    val state by stateFlow.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text("Cargando datos...", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Error al cargar datos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            state.error ?: "Error desconocido",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = { /* Retry logic */ }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Reintentar")
                        }
                    }
                }
            }

            else -> {
                DashboardContent(
                    state = state,
                    onAddIncomeClick = onAddIncomeClick,
                    onAddExpenseClick = onAddExpenseClick,
                    onOpenTxDetail = onOpenTxDetail
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    state: DashboardUiState,
    onAddIncomeClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onOpenTxDetail: (Long) -> Unit
) {
    val totalIncome = state.transactions.filter { it.amount > 0 }.sumOf { it.amount }
    val totalExpenseAbs = state.transactions.filter { it.amount < 0 }.sumOf { -it.amount }
    val balance = totalIncome - totalExpenseAbs
    val df = remember { DecimalFormat("#,##0.00") }
    fun q(v: Double) = "Q ${df.format(v)}"

    val financialData = listOf(
        FinancialData("Gastos", q(totalExpenseAbs), SoftRed),
        FinancialData("Ingresos", q(totalIncome), SoftGreen),
        FinancialData("Balance", q(balance), SoftBlue)
    )

    val maxBarValue = max(1.0, max(totalIncome, max(totalExpenseAbs, abs(balance))))
    fun barHeightFor(value: Double): Dp {
        val minBar = 40.dp
        val maxBar = 120.dp
        val ratio = (value / maxBarValue).coerceIn(0.0, 1.0)
        return (minBar + (maxBar - minBar) * ratio.toFloat())
    }

    val months = (5 downTo 0).map { LocalDate.now().minusMonths(it.toLong()) }
    val monthlyData = months.map { m ->
        val total = state.transactions.filter {
            it.dateText.startsWith("${m.year}-${m.monthValue.toString().padStart(2, '0')}")
        }.sumOf { it.amount }
        val label = m.month.getDisplayName(TextStyle.SHORT, Locale("es")).replaceFirstChar { it.uppercase() }
        MonthlyPoint1(label, total.toFloat())
    }
    val maxAbsMonthly = monthlyData.maxOfOrNull { abs(it.value) } ?: 1f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Resumen Financiero", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Info, contentDescription = "info", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 220.dp)
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Resumen mensual", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    financialData.forEach { data ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(55.dp)
                                    .height(barHeightFor(data.amount.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0))
                                    .drawBehind { drawRect(data.color, alpha = 0.85f) }
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(data.amount, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(data.label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 250.dp)
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Tendencia de Balance", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val pts = monthlyData.mapIndexed { i, p ->
                            Offset(
                                x = (w / (monthlyData.size - 1)) * i,
                                y = h - ((p.value / (maxAbsMonthly * 1.1f)) * h / 2f + h / 2f)
                            )
                        }
                        for (i in 0 until pts.size - 1) {
                            drawLine(color = SoftBlue, start = pts[i], end = pts[i + 1], strokeWidth = 4.dp.toPx())
                        }
                        pts.forEach { p ->
                            drawCircle(color = SoftBlue, radius = 6.dp.toPx(), center = p)
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    monthlyData.forEach { point ->
                        Text(
                            point.month,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Últimos movimientos",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        if (state.transactions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No hay transacciones aún",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Agrega tu primer ingreso o gasto",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            state.transactions.forEach { tx ->
                ListItem(
                    headlineContent = { Text(tx.description) },
                    supportingContent = { Text(tx.dateText) },
                    trailingContent = {
                        val amountAbs = kotlin.math.abs(tx.amount)
                        val sign = if (tx.amount >= 0) "" else "-"
                        Text(
                            "$sign Q ${df.format(amountAbs)}",
                            color = if (tx.amount >= 0) SoftGreen else SoftRed,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenTxDetail(tx.id) }
                        .padding(horizontal = 4.dp)
                )
                HorizontalDivider()
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
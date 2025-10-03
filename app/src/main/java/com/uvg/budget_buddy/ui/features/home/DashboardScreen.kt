package com.uvg.budget_buddy.ui.features.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uvg.budget_buddy.ui.theme.Budget_buddyTheme
import com.uvg.budget_buddy.ui.theme.SoftBlue
import com.uvg.budget_buddy.ui.theme.SoftGreen
import com.uvg.budget_buddy.ui.theme.SoftRed

data class FinancialData(val label: String, val amount: String, val color: Color)
data class MonthlyPoint1(val month: String, val value: Float)

@Composable
fun DashboardScreen(
    onAddIncomeClick: () -> Unit,
    onAddExpenseClick: () -> Unit
) {
    val financialData = listOf(
        FinancialData("Gastos",   "Q 348.28", SoftRed),    // rojo suave
        FinancialData("Ingresos", "Q 1,200.00", SoftGreen),// verde suave
        FinancialData("Balance",  "Q 850.75", SoftBlue)    // azul suave
    )
    val monthlyData = listOf(
        MonthlyPoint1("Ene", 800f),
        MonthlyPoint1("Mar", 850f),
        MonthlyPoint1("May", 900f),
        MonthlyPoint1("Jul", 950f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Resumen Financiero", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Info, contentDescription = "Información", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Resumen mensual", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    financialData.forEach { data ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(
                                        when (data.label) {
                                            "Gastos" -> 80.dp
                                            "Ingresos" -> 120.dp
                                            "Balance" -> 100.dp
                                            else -> 60.dp
                                        }
                                    )
                                    .drawBehind { drawRect(color = data.color) }
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(text = data.amount, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = data.label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Tendencia de Balance", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 16.dp))
                Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width; val h = size.height
                        val pts = monthlyData.mapIndexed { i, p ->
                            Offset(
                                x = (w / (monthlyData.size - 1)) * i,
                                y = h - (p.value / 1000f) * h
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    monthlyData.forEach { point ->
                        Text(text = point.month, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    Budget_buddyTheme {
        DashboardScreen({}, {})
    }
}
package com.uvg.budget_buddy.ui.features.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
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

data class FinancialData(
    val label: String,
    val amount: String,
    val color: Color
)

data class MonthlyPoint1(
    val month: String,
    val value: Float
)

@Composable
fun DashboardScreen(
    onAddIncomeClick: () -> Unit,
    onAddExpenseClick: () -> Unit
) {
    val financialData = listOf(
        FinancialData("Gastos", "Q 348.28", Color(0xFFE74C3C)),
        FinancialData("Ingresos", "Q 1,200.00", Color(0xFF4A90E2)),
        FinancialData("Balance", "Q 850.75", Color(0xFF27AE60))
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
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Resumen Financiero",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Información",
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Financial Summary Chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Resumen mensual",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    financialData.forEach { data ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
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
                                    .drawBehind {
                                        drawRect(color = data.color)
                                    }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = data.amount,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = data.label,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Line Chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Tendencia de Balance",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val width = size.width
                        val height = size.height

                        val points = monthlyData.mapIndexed { index, point ->
                            Offset(
                                x = (width / (monthlyData.size - 1)) * index,
                                y = height - (point.value / 1000f) * height
                            )
                        }

                        for (i in 0 until points.size - 1) {
                            drawLine(
                                color = Color(0xFF4A90E2),
                                start = points[i],
                                end = points[i + 1],
                                strokeWidth = 4.dp.toPx()
                            )
                        }

                        points.forEach { point ->
                            drawCircle(
                                color = Color(0xFF4A90E2),
                                radius = 6.dp.toPx(),
                                center = point
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    monthlyData.forEach { point ->
                        Text(
                            text = point.month,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
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
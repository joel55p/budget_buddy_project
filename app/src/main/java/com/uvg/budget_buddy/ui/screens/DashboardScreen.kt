package com.uvg.budget_buddy.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FinancialData(
    val label: String,
    val amount: String,
    val color: Color
)

data class MonthlyPoint(
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
        MonthlyPoint("Ene", 800f),
        MonthlyPoint("Mar", 850f),
        MonthlyPoint("May", 900f),
        MonthlyPoint("Jul", 950f)
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
                Icons.Default.Info,
                contentDescription = "Información",
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Financial Summary Chart (Bar Chart Representation)
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
                            // Bar representation
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

        Spacer(modifier = Modifier.height(32)

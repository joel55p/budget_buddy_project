package com.uvg.budget_buddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Text(
            text = "✱",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A90E2)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "BudgetBuddy",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Bienvenido a BudgetBuddy",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Inicia sesión para gestionar tus finanzas.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Line Chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Simple line chart representation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
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

                        // Draw line
                        for (i in 0 until points.size - 1) {
                            drawLine(
                                color = Color(0xFF4A90E2),
                                start = points[i],
                                end = points[i + 1],
                                strokeWidth = 4.dp.toPx()
                            )
                        }

                        // Draw points
                        points.forEach { point ->
                            drawCircle(
                                color = Color(0xFF4A90E2),
                                radius = 6.dp.toPx(),
                                center = point
                            )
                        }
                    }
                }

                // Month labels
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

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Navigation
        BottomNavigation(
            currentScreen = "dashboard",
            onAddIncomeClick = onAddIncomeClick,
            onAddExpenseClick = onAddExpenseClick
        )
    }
}

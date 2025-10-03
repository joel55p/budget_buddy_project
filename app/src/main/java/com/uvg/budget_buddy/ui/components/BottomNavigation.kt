package com.uvg.budget_buddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigation(
    currentScreen: String,
    onHomeClick: () -> Unit,
    onAddIncomeClick: () -> Unit,
    onAddExpenseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            IconButton(onClick = onHomeClick) {                         // <--- usar callback
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Inicio",
                    tint = if (currentScreen == "dashboard")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "Inicio",
                fontSize = 12.sp,
                color = if (currentScreen == "dashboard")
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Income
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            IconButton(onClick = onAddIncomeClick) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Ingreso",
                    tint = if (currentScreen == "ingreso")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "Ingreso",
                fontSize = 12.sp,
                color = if (currentScreen == "ingreso")
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Expense
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            IconButton(onClick = onAddExpenseClick) {
                Text(
                    "≡",
                    fontSize = 20.sp,
                    color = if (currentScreen == "gasto")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "Gasto",
                fontSize = 12.sp,
                color = if (currentScreen == "gasto")
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
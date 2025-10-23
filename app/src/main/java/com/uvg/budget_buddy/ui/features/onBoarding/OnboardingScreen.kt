package com.uvg.budget_buddy.ui.features.onBoarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(
    onStartClick: () -> Unit
) {
    var budgetAmount by remember { mutableStateOf("500") }
    var selectedPeriod by remember { mutableStateOf("Mensual") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("✱ BudgetBuddy", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(12.dp))
        Text("Configura tu primer presupuesto para empezar a ahorrar.")

        Spacer(Modifier.height(24.dp))
        OutlinedTextField(value = budgetAmount, onValueChange = { budgetAmount = it }, label = { Text("Monto") })
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = selectedPeriod, onValueChange = { selectedPeriod = it }, label = { Text("Periodo") })

        Spacer(Modifier.height(24.dp))
        Button(onClick = onStartClick, modifier = Modifier.fillMaxWidth()) { Text("Empezar") }
    }
}

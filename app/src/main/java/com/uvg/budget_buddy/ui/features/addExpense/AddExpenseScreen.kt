package com.uvg.budget_buddy.ui.features.addExpense

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.uvg.budget_buddy.ui.theme.Budget_buddyTheme

@Composable
fun AddExpenseScreen(
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var amount by remember { mutableStateOf("0.00") }
    var category by remember { mutableStateOf("Selecciona una categoría") }
    var date by remember { mutableStateOf("September 11th, 2025") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {


        Text("Agregar Gasto", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(24.dp))

        Text("Monto del Gasto", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Text("Categoría", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = category,
            onValueChange = {  },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )

        Spacer(Modifier.height(16.dp))

        Text("Fecha del Gasto", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = date,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text("Guardar Gasto")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddExpenseScreenPreview() {
    Budget_buddyTheme {
        AddExpenseScreen({}, {})
    }
}
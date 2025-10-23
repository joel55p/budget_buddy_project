package com.uvg.budget_buddy.ui.features.addExpense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AddExpenseScreen(
    state: StateFlow<AddExpenseUiState>,
    onEvent: (AddExpenseEvent) -> Unit,
    onBackClick: () -> Unit,
    onSaved: () -> Unit
) {
    val ui by state.collectAsStateWithLifecycle()
    val snack = remember { SnackbarHostState() }

    LaunchedEffect(ui.saved) {
        if (ui.saved) {
            snack.showSnackbar("Gasto guardado")
            onEvent(AddExpenseEvent.SavedConsumed) // limpia y permite agregar otro
            onSaved()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snack) }) { inner ->
        Column(Modifier.fillMaxSize().padding(inner).padding(24.dp)) {
            Text("Agregar Gasto", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = ui.amount,
                onValueChange = { onEvent(AddExpenseEvent.AmountChanged(it)) },
                label = { Text("Monto del gasto") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = ui.description,
                onValueChange = { onEvent(AddExpenseEvent.DescriptionChanged(it)) },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            ui.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = { onEvent(AddExpenseEvent.Save) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !ui.isSaving
            ) {
                if (ui.isSaving) CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                else Text("Guardar ")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onBackClick, modifier = Modifier.fillMaxWidth(), enabled = !ui.isSaving) {
                Text("Volver")
            }
        }
    }
}

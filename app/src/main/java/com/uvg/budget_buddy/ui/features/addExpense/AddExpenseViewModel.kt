package com.uvg.budget_buddy.ui.features.addExpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.budget_buddy.data.repo.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddExpenseUiState(
    val amount: String = "",
    val description: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

sealed interface AddExpenseEvent {
    data class AmountChanged(val value: String) : AddExpenseEvent
    data class DescriptionChanged(val value: String) : AddExpenseEvent
    data object Save : AddExpenseEvent
    data object SavedConsumed : AddExpenseEvent // <--- NUEVO
}

class AddExpenseViewModel(private val repo: BudgetRepository) : ViewModel() {
    private val _state = MutableStateFlow(AddExpenseUiState())
    val state: StateFlow<AddExpenseUiState> = _state

    fun onEvent(e: AddExpenseEvent) = when (e) {
        is AddExpenseEvent.AmountChanged -> _state.update { it.copy(amount = e.value, error = null) }
        is AddExpenseEvent.DescriptionChanged -> _state.update { it.copy(description = e.value, error = null) }
        AddExpenseEvent.Save -> save()
        AddExpenseEvent.SavedConsumed -> _state.update {
            it.copy(saved = false, amount = "", description = "", error = null) // limpia form
        }
    }

    private fun save() {
        val value = _state.value.amount.toDoubleOrNull()
        if (value == null || value <= 0.0) {
            _state.update { it.copy(error = "Monto inválido") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val result = repo.addExpense(value, _state.value.description)
            _state.update {
                if (result.isSuccess)
                    it.copy(
                        isSaving = false,
                        saved = true,
                        amount = "",             
                        description = ""
                    )
                else it.copy(isSaving = false, error = result.exceptionOrNull()?.message ?: "Error al guardar")
            }
        }
    }

}

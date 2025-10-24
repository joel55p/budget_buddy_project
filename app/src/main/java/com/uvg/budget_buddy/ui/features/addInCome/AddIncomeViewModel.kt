package com.uvg.budget_buddy.ui.features.addInCome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.budget_buddy.data.repo.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddIncomeUiState(
    val amount: String = "",
    val description: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false    // se pone true al guardar; la UI lo consume
)

sealed interface AddIncomeEvent {
    data class AmountChanged(val value: String) : AddIncomeEvent
    data class DescriptionChanged(val value: String) : AddIncomeEvent
    data object Save : AddIncomeEvent
    data object SavedConsumed : AddIncomeEvent // <--- NUEVO
}

class AddIncomeViewModel(private val repo: BudgetRepository) : ViewModel() {
    private val _state = MutableStateFlow(AddIncomeUiState())
    val state: StateFlow<AddIncomeUiState> = _state

    fun onEvent(e: AddIncomeEvent) = when (e) {
        is AddIncomeEvent.AmountChanged -> _state.update { it.copy(amount = e.value, error = null) }
        is AddIncomeEvent.DescriptionChanged -> _state.update { it.copy(description = e.value, error = null) }
        AddIncomeEvent.Save -> save()
        AddIncomeEvent.SavedConsumed -> _state.update {
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
            val result = repo.addIncome(value, _state.value.description)
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

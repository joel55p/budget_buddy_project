package com.uvg.budget_buddy.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.budget_buddy.data.model.Transaction
import com.uvg.budget_buddy.data.repo.BudgetRepository
import com.uvg.budget_buddy.data.repo.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class DashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0
)

sealed interface DashboardEvent {
    data object Retry : DashboardEvent
}

class DashboardViewModel(private val repo: BudgetRepository) : ViewModel() {
    private val _state = MutableStateFlow(DashboardUiState(isLoading = true))
    val state: StateFlow<DashboardUiState> = _state

    init {
        // Pequeño delay para dar tiempo a que Firebase se inicialice
        viewModelScope.launch {
            delay(500)
            observe()
        }
    }

    private fun observe() {
        viewModelScope.launch {
            repo.observeTransactions()
                .catch { e ->
                    // Capturar errores silenciosamente en la primera carga
                    _state.update {
                        DashboardUiState(
                            isLoading = false,
                            transactions = emptyList(),
                            error = null  // No mostrar error en primera carga
                        )
                    }
                }
                .collect { res ->
                    when (res) {
                        is Resource.Loading -> {
                            _state.update { it.copy(isLoading = true, error = null) }
                        }
                        is Resource.Success -> {
                            val txs = res.data
                            val inc = txs.filter { it.amount > 0 }.sumOf { it.amount }
                            val exp = -txs.filter { it.amount < 0 }.sumOf { -it.amount }
                            _state.value = DashboardUiState(
                                isLoading = false,
                                transactions = txs,
                                totalIncome = inc,
                                totalExpense = exp,
                                balance = inc + exp,
                                error = null
                            )
                        }
                        is Resource.Error -> {
                            // Solo mostrar error si ya había datos cargados anteriormente
                            val currentTxs = _state.value.transactions
                            if (currentTxs.isNotEmpty()) {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        error = res.message
                                    )
                                }
                            } else {
                                // Primera carga: no mostrar error, solo estado vacío
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        error = null
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }

    fun onEvent(e: DashboardEvent) {
        if (e is DashboardEvent.Retry) {
            _state.update { it.copy(isLoading = true, error = null) }
            observe()
        }
    }
}
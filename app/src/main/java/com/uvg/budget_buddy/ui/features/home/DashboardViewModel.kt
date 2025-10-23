package com.uvg.budget_buddy.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.budget_buddy.data.model.Transaction
import com.uvg.budget_buddy.data.repo.BudgetRepository
import com.uvg.budget_buddy.data.repo.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0
)

sealed interface DashboardEvent { data object Retry : DashboardEvent }

class DashboardViewModel(private val repo: BudgetRepository) : ViewModel() {
    private val _state = MutableStateFlow(DashboardUiState(isLoading = true))
    val state: StateFlow<DashboardUiState> = _state

    init { observe() }

    private fun observe() {
        viewModelScope.launch {
            repo.observeTransactions().collect { res ->
                when (res) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true, error = null) }
                    is Resource.Success -> {
                        val txs = res.data
                        val inc = txs.filter { it.amount > 0 }.sumOf { it.amount }
                        val exp = -txs.filter { it.amount < 0 }.sumOf { -it.amount }
                        _state.value = DashboardUiState(
                            isLoading = false,
                            transactions = txs,
                            totalIncome = inc,
                            totalExpense = exp,
                            balance = inc + exp
                        )
                    }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, error = res.message) }
                }
            }
        }
    }

    fun onEvent(e: DashboardEvent) {
        if (e is DashboardEvent.Retry) observe()
    }
}

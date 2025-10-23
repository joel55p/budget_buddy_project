package com.uvg.budget_buddy.ui.features.settings

import androidx.lifecycle.ViewModel
import com.uvg.budget_buddy.data.repo.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(private val repo: BudgetRepository) : ViewModel() {
    private val _simulateErrors = MutableStateFlow(false)
    val simulateErrors: StateFlow<Boolean> = _simulateErrors

    fun toggleSimulateErrors(newValue: Boolean) {
        _simulateErrors.value = newValue
        repo.setSimulateErrors(newValue)
    }
}

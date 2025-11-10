package com.uvg.budget_buddy.ui.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.budget_buddy.data.local.preferences.UserPreferencesDataStore
import com.uvg.budget_buddy.data.repo.AuthRepository
import com.uvg.budget_buddy.data.repo.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repo: BudgetRepository,
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferencesDataStore
) : ViewModel() {

    private val _simulateErrors = MutableStateFlow(false)
    val simulateErrors: StateFlow<Boolean> = _simulateErrors

    val userEmail = userPreferences.userEmail

    init {
        viewModelScope.launch {
            userPreferences.simulateErrors.collect { enabled ->
                _simulateErrors.value = enabled
                repo.setSimulateErrors(enabled)
            }
        }
    }

    fun toggleSimulateErrors(newValue: Boolean) {
        viewModelScope.launch {
            userPreferences.setSimulateErrors(newValue)
            _simulateErrors.value = newValue
            repo.setSimulateErrors(newValue)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
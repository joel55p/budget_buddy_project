package com.uvg.budget_buddy.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.budget_buddy.data.local.preferences.UserPreferencesDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferencesDataStore(application)

    val isDarkMode = userPreferences.isDarkMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun setTheme(dark: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkMode(dark)
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            userPreferences.setDarkMode(!isDarkMode.value)
        }
    }
}
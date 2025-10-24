package com.uvg.budget_buddy.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel : ViewModel() {
    private val TAG = "ThemeViewModel"

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleTheme() {
        val newValue = !_isDarkMode.value
        Log.d(TAG, "toggleTheme() called. Old value: ${_isDarkMode.value}, New value: $newValue")
        _isDarkMode.value = newValue
    }

    fun setTheme(dark: Boolean) {
        Log.d(TAG, "setTheme() called with dark=$dark. Current value: ${_isDarkMode.value}")
        _isDarkMode.value = dark
        Log.d(TAG, "setTheme() completed. New value: ${_isDarkMode.value}")
    }
}
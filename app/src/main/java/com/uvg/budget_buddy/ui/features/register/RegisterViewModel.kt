package com.uvg.budget_buddy.ui.features.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.budget_buddy.data.repo.AuthRepository
import com.uvg.budget_buddy.data.repo.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegisterSuccessful: Boolean = false
)

sealed interface RegisterEvent {
    data class NameChanged(val value: String) : RegisterEvent
    data class EmailChanged(val value: String) : RegisterEvent
    data class PasswordChanged(val value: String) : RegisterEvent
    data class ConfirmPasswordChanged(val value: String) : RegisterEvent
    data object Register : RegisterEvent
    data object ClearError : RegisterEvent
}

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.NameChanged -> {
                _state.update { it.copy(name = event.value, error = null) }
            }
            is RegisterEvent.EmailChanged -> {
                _state.update { it.copy(email = event.value, error = null) }
            }
            is RegisterEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.value, error = null) }
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = event.value, error = null) }
            }
            RegisterEvent.Register -> register()
            RegisterEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun register() {
        val name = _state.value.name.trim()
        val email = _state.value.email.trim()
        val password = _state.value.password
        val confirmPassword = _state.value.confirmPassword

        if (name.isBlank()) {
            _state.update { it.copy(error = "El nombre es requerido") }
            return
        }

        if (email.isBlank()) {
            _state.update { it.copy(error = "El email es requerido") }
            return
        }

        if (password.isBlank()) {
            _state.update { it.copy(error = "La contraseña es requerida") }
            return
        }

        if (password.length < 6) {
            _state.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        if (password != confirmPassword) {
            _state.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = authRepository.signUp(email, password)) {
                is AuthResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRegisterSuccessful = true,
                            error = null
                        )
                    }
                }
                is AuthResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}
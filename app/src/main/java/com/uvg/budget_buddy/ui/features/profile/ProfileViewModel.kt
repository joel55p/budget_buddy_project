package com.uvg.budget_buddy.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.budget_buddy.data.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {  // try-catch
                val user = authRepository.currentUser
                if (user != null) {
                    // Extraer el nombre del email
                    val emailName = user.email?.substringBefore("@") ?: "Usuario"

                    // Convertir "juan.perez" a "Juan Perez"
                    val displayName = user.displayName ?: emailName
                        .split(".", "_", "-")
                        .joinToString(" ") { part ->
                            part.replaceFirstChar { it.uppercase() }
                        }

                    _state.update {
                        it.copy(
                            name = displayName,
                            email = user.email ?: "",
                            isLoading = false,
                            error = null  // ← AGREGADO
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            name = "Usuario",
                            email = "No disponible",
                            isLoading = false,
                            error = null  // ← AGREGADO
                        )
                    }
                }
            } catch (e: Exception) {  // manejo de errores
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar datos"
                    )
                }
            }
        }
    }

    fun refreshUserData() {
        loadUserData()
    }
}
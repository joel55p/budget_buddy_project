package com.uvg.budget_buddy.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.uvg.budget_buddy.data.local.preferences.UserPreferencesDataStore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    data class Success(val user: FirebaseUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val userPreferences: UserPreferencesDataStore
) {
    val currentUser: FirebaseUser? get() = auth.currentUser

    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)

        // Enviar estado actual inmediatamente
        trySend(auth.currentUser)

        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                // Guardar datos del usuario en preferencias
                userPreferences.saveUserData(user.uid, email)
                AuthResult.Success(user)
            } ?: AuthResult.Error("Error al crear usuario")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error desconocido al registrar")
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                // Guardar datos del usuario en preferencias
                userPreferences.saveUserData(user.uid, email)
                AuthResult.Success(user)
            } ?: AuthResult.Error("Error al iniciar sesión")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error desconocido al iniciar sesión")
        }
    }

    suspend fun signOut() {
        auth.signOut()
        userPreferences.clearUserData()
    }

    suspend fun reauthenticateUser(password: String): AuthResult {
        val user = auth.currentUser ?: return AuthResult.Error("Usuario no autenticado")

        val credential = com.google.firebase.auth.EmailAuthProvider
            .getCredential(user.email ?: return AuthResult.Error("Email no disponible"), password)

        return try {
            user.reauthenticate(credential).await()
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al reautenticar usuario")
        }
    }

    suspend fun updatePassword(newPassword: String): AuthResult {
        val user = auth.currentUser ?: return AuthResult.Error("Usuario no autenticado")

        return try {
            user.updatePassword(newPassword).await()
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al actualizar contraseña")
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): AuthResult {
        return try {
            val user = auth.currentUser ?: return AuthResult.Error("Usuario no logueado")

            val email = user.email ?: return AuthResult.Error("Email no disponible")

            // Re-autenticar al usuario
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, oldPassword)
            user.reauthenticate(credential).await()

            // Cambiar la contraseña
            user.updatePassword(newPassword).await()

            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al cambiar contraseña")
        }
    }

    fun isUserLoggedIn(): Boolean = currentUser != null
}
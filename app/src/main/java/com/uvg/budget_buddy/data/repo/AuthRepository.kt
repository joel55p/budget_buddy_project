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

        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
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

    fun isUserLoggedIn(): Boolean = currentUser != null
}
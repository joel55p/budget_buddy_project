package com.uvg.budget_buddy.ui.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // Nombre a mostrar:
        // - si hay nombre, usamos la primera "palabra"
        // - si no hay nombre, usamos el email
        // - si tampoco hay email, usamos "Usuario"
        val displayName = when {
            state.name.isNotBlank() ->
                state.name.trim().substringBefore(" ")
            state.email.isNotBlank() ->
                state.email.substringBefore("@")
            else -> "Usuario"
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Nombre grande
            Text(
                text = displayName,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(4.dp))

            // Subtítulo pequeño (opcional)
            if (state.name.isNotBlank() && state.name != displayName) {
                Text(
                    text = state.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(24.dp))

            // Tarjeta de información de cuenta
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    ListItem(
                        leadingContent = {
                            Icon(Icons.Default.Email, contentDescription = null)
                        },
                        headlineContent = { Text("Email") },
                        supportingContent = {
                            Text(
                                text = state.email.ifBlank { "No disponible" }
                            )
                        }
                    )

                    HorizontalDivider()

                    ListItem(
                        leadingContent = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        headlineContent = { Text("Tipo de cuenta") },
                        supportingContent = {
                            Text("Cuenta personal")
                        }
                    )
                }
            }

            // Errores
            state.error?.let { error ->
                Spacer(Modifier.height(16.dp))
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.weight(1f))

            // Ya no hay botón de "Editar perfil"
            // La gestión de contraseña se hace en Settings → Cambiar contraseña.
        }
    }
}
package com.uvg.budget_buddy.ui.features.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    simulateErrors: StateFlow<Boolean>,
    onToggleSimulateErrors: (Boolean) -> Unit,
    currentDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val sim by simulateErrors.collectAsStateWithLifecycle()
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {

            Text("Cuenta", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Card(Modifier.fillMaxWidth()) {
                Column {
                    Item(
                        icon = Icons.Default.Person,
                        title = "Editar Perfil",
                        subtitle = "Actualiza tu información personal",
                        onClick = { /* TODO: navegar a editar perfil */ }
                    )
                    HorizontalDivider()
                    Item(
                        icon = Icons.Default.Lock,
                        title = "Cambiar Contraseña",
                        subtitle = "Actualiza tu contraseña de seguridad",
                        onClick = { /* TODO: cambiar contraseña */ }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ===== Preferencias =====
            Text("Preferencias", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Card(Modifier.fillMaxWidth()) {
                Column {
                    SettingsSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "Notificaciones",
                        subtitle = "Recibe alertas y recordatorios",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    HorizontalDivider()
                    SettingsSwitchItem(
                        icon = Icons.Default.DarkMode,
                        title = "Modo Oscuro",
                        subtitle = "Cambia el tema de la aplicación",
                        checked = currentDarkMode,
                        onCheckedChange = onToggleDarkMode
                    )
                    HorizontalDivider()
                    SettingsSwitchItem(
                        icon = Icons.Default.ReportProblem,
                        title = "Simular errores",
                        subtitle = "Activa fallas de red simuladas",
                        checked = sim,
                        onCheckedChange = onToggleSimulateErrors
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ===== Sesión =====
            Text("Sesión", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }

    // ===== Diálogo de logout =====
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Seguro que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    SwitchItem(icon, title, subtitle, checked, onCheckedChange)
}


@Composable
private fun Item(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
private fun SwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
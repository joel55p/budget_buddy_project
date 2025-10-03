package com.uvg.budget_buddy.ui.features.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uvg.budget_buddy.ui.theme.Budget_buddyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit, // Regresa a pantalla anterior
    onLogout: () -> Unit // Cierra sesión
) {
    // Estados locales para los switches
    var showLogoutDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            // seccion 1: cuenta
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            SectionTitle("Cuenta")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.Person,
                        title = "Editar Perfil",
                        subtitle = "Actualiza tu información personal",
                        onClick = { }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = "Cambiar Contraseña",
                        subtitle = "Actualiza tu contraseña de seguridad",
                        onClick = { }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            SectionTitle("Preferencias") //va a ser la seccion 2

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
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
                        icon = Icons.Default.Star,
                        title = "Modo Oscuro",
                        subtitle = "Cambia el tema de la aplicación",
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.Settings,
                        title = "Moneda",
                        subtitle = "Quetzales (Q)",
                        onClick = { }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            SectionTitle("Datos y Privacidad") //seccion 3

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.AccountBox,
                        title = "Exportar Datos",
                        subtitle = "Descarga tu información",
                        onClick = { }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.Delete,
                        title = "Eliminar Cuenta",
                        subtitle = "Borra tu cuenta permanentemente",
                        onClick = { },
                        textColor = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            SectionTitle("Información") //seccion 4

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "Acerca de",
                        subtitle = "Versión 1.0.0",
                        onClick = { }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Default.Email,
                        title = "Soporte",
                        subtitle = "Contacta con nosotros",
                        onClick = { }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
            // Botón de cerrar sesión
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
    // Diálogo de confirmación
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()// Ejecuta la acción de logout
                    }
                ) {
                    Text("Confirmar", color = MaterialTheme.colorScheme.error)
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
// Componente para títulos de sección
@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@Composable
fun SettingsItem(// Componente clickeable para items de configuración
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Text(
                subtitle,
                fontSize = 12.sp,
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
fun SettingsSwitchItem(// Componente con switch para items toggleables
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            // Switch interactivo
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    Budget_buddyTheme {
        SettingsScreen(
            onBackClick = {},
            onLogout = {}
        )
    }
}
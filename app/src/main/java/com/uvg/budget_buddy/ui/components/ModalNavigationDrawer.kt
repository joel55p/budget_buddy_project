package com.uvg.budget_buddy.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onCategories: () -> Unit,
    onReports: () -> Unit,
    onLogout: () -> Unit,
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(8.dp))
        Text("Menú", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))

        NavigationDrawerItem(label = { Text("Perfil") },       selected = false, onClick = onProfile)
        NavigationDrawerItem(label = { Text("Configuración") },selected = false, onClick = onSettings)
        NavigationDrawerItem(label = { Text("Categorías") },   selected = false, onClick = onCategories)
        NavigationDrawerItem(label = { Text("Reportes") },     selected = false, onClick = onReports)

        Divider()
        NavigationDrawerItem(label = { Text("Cerrar sesión") },selected = false, onClick = onLogout)
    }
}
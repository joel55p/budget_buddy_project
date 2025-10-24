package com.uvg.budget_buddy.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(8.dp))
        Text("Menú", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))

        NavigationDrawerItem(label = { Text("Perfil") }, selected = false, onClick = onProfile)
        NavigationDrawerItem(label = { Text("Configuración") }, selected = false, onClick = onSettings)

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        NavigationDrawerItem(label = { Text("Cerrar sesión") }, selected = false, onClick = onLogout)
    }
}
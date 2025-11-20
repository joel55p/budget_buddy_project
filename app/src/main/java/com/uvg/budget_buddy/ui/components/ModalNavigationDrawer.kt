package com.uvg.budget_buddy.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.uvg.budget_buddy.navigation.Screen

@Composable
fun AppDrawer(
    currentRoute: String?,
    onHome: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    val isHomeSelected     = currentRoute == Screen.Dashboard.route
    val isProfileSelected  = currentRoute == Screen.Profile.route
    val isSettingsSelected = currentRoute == Screen.Settings.route

    val itemColors = NavigationDrawerItemDefaults.colors(
        selectedTextColor        = MaterialTheme.colorScheme.primary,
        unselectedTextColor      = MaterialTheme.colorScheme.onSurface,
        selectedIconColor        = MaterialTheme.colorScheme.primary,
        unselectedIconColor      = MaterialTheme.colorScheme.onSurfaceVariant,
        selectedContainerColor   = Color.Transparent,
        unselectedContainerColor = Color.Transparent
    )

    ModalDrawerSheet {

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Menú",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(16.dp)
        )

        NavigationDrawerItem(
            label = { Text("Inicio") },
            selected = isHomeSelected,
            onClick = onHome,
            colors = itemColors
        )

        NavigationDrawerItem(
            label = { Text("Perfil") },
            selected = isProfileSelected,
            onClick = onProfile,
            colors = itemColors
        )

        NavigationDrawerItem(
            label = { Text("Configuración") },
            selected = isSettingsSelected,
            onClick = onSettings,
            colors = itemColors
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        NavigationDrawerItem(
            label = { Text("Cerrar sesión") },
            selected = false,
            onClick = onLogout,
            colors = itemColors
        )
    }
}
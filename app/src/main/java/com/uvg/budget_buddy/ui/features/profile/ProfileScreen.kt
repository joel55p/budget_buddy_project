package com.uvg.budget_buddy.ui.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onEditProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier.fillMaxSize().padding(inner).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(120.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(64.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text("Juan Pérez", fontSize = 22.sp)

            Spacer(Modifier.height(24.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    ListItem(
                        leadingContent = { Icon(Icons.Default.Email, contentDescription = null) },
                        headlineContent = { Text("Email") },
                        supportingContent = { Text("juan.perez@email.com") }
                    )
                    HorizontalDivider()
                    ListItem(
                        leadingContent = { Icon(Icons.Default.Lock, contentDescription = null) },
                        headlineContent = { Text("Contraseña") },
                        supportingContent = { Text("••••••••") }
                    )
                }
            }

            Spacer(Modifier.weight(1f))
            Button(onClick = onEditProfile, modifier = Modifier.fillMaxWidth()) { Text("Editar Perfil") }
        }
    }
}
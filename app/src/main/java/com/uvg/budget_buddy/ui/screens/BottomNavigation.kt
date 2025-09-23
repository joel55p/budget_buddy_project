package com.uvg.budget_buddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigation(
    currentScreen: String = "",
    onAddIncomeClick: (() -> Unit)? = null,
    onAddExpenseClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Default.Home,
                contentDescription = "Inicio",
                tint = if (currentScreen == "dashboard") Color(0xFF4A90E2) else Color.Gray
            )
            Text(
                "Inicio",
                fontSize = 12.sp,
                color = if (currentScreen == "dashboard") Color(0xFF4A90E2) else Color.Gray
            )
        }

        // Income
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = { onAddIncomeClick?.invoke() }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Ingreso",
                    tint = if (currentScreen == "ingreso") Color(0xFF4A90E2) else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                "Ingreso",
                fontSize = 12.sp,
                color = if (currentScreen == "ingreso") Color(0xFF4A90E2) else Color.Gray
            )
        }

        // Expense
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = { onAddExpenseClick?.invoke() }
            ) {
                Text(
                    "≡",
                    fontSize = 24.sp,
                    color = if (currentScreen == "gasto") Color(0xFF4A90E2) else Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                "Gasto",
                fontSize = 12.sp,
                color = if (currentScreen == "gasto") Color(0xFF4A90E2) else Color.Gray
            )
        }
    }
}.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Correo electrónico o Usuario",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("correo@ejemplo.com") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Contraseña",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("••••••••") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
        onClick = onLoginClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
        ) {
            Text(
                text = "Iniciar sesión",
                color = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { /* Recuperar contraseña */ }) {
            Text(
                text = "Recuperar contraseña",
                color = Color(0xFF4A90E2)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onRegisterClick) {
            Text(
                text = "¿No tienes cuenta? Regístrate",
                color = Color(0xFF4A90E2)
            )
        }
        }
        }


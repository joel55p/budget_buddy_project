package com.uvg.budget_buddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.uvg.budget_buddy.navigation.BudgetBuddyApp
import com.uvg.budget_buddy.ui.theme.Budget_buddyTheme

// MainActivity es la clase principal, es el punto de entrada de la aplicación
//es decir que inicializa la aplicacion, configura el tema visual, llama  al sistema  de nav principal
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { // Establece el contenido de la Activity con Jetpack Compose
            Budget_buddyTheme {
                // Surface es el contenedor base con el color de fondo del tema
                Surface(
                    modifier = Modifier.fillMaxSize(), // Ocupa toda la pantalla
                    color = MaterialTheme.colorScheme.background // Utiliza el color de fondo definido en el tema
                ) {
                    BudgetBuddyApp() // Llama a la función de la aplicación BudgetBuddy que maneja la navegación
                }
            }
        }
    }
}

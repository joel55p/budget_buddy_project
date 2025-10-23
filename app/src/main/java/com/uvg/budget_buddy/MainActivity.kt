package com.uvg.budget_buddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.uvg.budget_buddy.navigation.BudgetBuddyApp
import com.uvg.budget_buddy.ui.theme.Budget_buddyTheme
import com.uvg.budget_buddy.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewModel para modo oscuro/claro
        val themeVm: ThemeViewModel by viewModels()

        setContent {
            val isDark by themeVm.isDarkMode.collectAsState()

            Budget_buddyTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pasa el ViewModel al árbol principal
                    BudgetBuddyApp(themeVm)
                }
            }
        }
    }
}



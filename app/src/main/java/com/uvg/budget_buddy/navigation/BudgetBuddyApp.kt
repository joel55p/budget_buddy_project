package com.uvg.budget_buddy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uvg.budget_buddy.ui.screens.*

@Composable
fun BudgetBuddyApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginClick = { navController.navigate("onboarding") },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterClick = { navController.navigate("onboarding") },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                onStartClick = { navController.navigate("dashboard") }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                onAddIncomeClick = { navController.navigate("add_income") },
                onAddExpenseClick = { navController.navigate("add_expense") }
            )
        }

        composable("add_income") {
            AddIncomeScreen(
                onSaveClick = { navController.navigate("dashboard") },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("add_expense") {
            AddExpenseScreen(
                onSaveClick = { navController.navigate("dashboard") },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
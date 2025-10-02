package com.uvg.budget_buddy.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.uvg.budget_buddy.ui.components.BottomNavigation
import com.uvg.budget_buddy.ui.features.login.LoginScreen
import com.uvg.budget_buddy.ui.features.register.RegisterScreen
import com.uvg.budget_buddy.ui.features.onBoarding.OnboardingScreen
import com.uvg.budget_buddy.ui.features.home.DashboardScreen
import com.uvg.budget_buddy.ui.features.addInCome.AddIncomeScreen
import com.uvg.budget_buddy.ui.features.addExpense.AddExpenseScreen

@Composable
fun BudgetBuddyApp() {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf(
                    Screen.Dashboard.route,
                    Screen.AddIncome.route,
                    Screen.AddExpense.route
                )
            ) {
                BottomNavigation(
                    currentScreen = currentRoute ?: Screen.Dashboard.route,
                    onHomeClick = { navController.navigate(Screen.Dashboard.route) },
                    onAddIncomeClick = { navController.navigate(Screen.AddIncome.route) },
                    onAddExpenseClick = { navController.navigate(Screen.AddExpense.route) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginClick = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Login.route) { inclusive = true } // saca Login del back stack
                        }
                    },
                    onRegisterClick = { navController.navigate(Screen.Register.route) }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterClick = { navController.popBackStack() }, // vuelve a Login
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onStartClick = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true } // saca Onboarding
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onAddIncomeClick = { navController.navigate(Screen.AddIncome.route) },
                    onAddExpenseClick = { navController.navigate(Screen.AddExpense.route) }
                )
            }

            composable(Screen.AddIncome.route) {
                AddIncomeScreen(
                    onSaveClick = { navController.navigate(Screen.Dashboard.route) },
                    onBackClick  = { navController.popBackStack() }
                )
            }

            composable(Screen.AddExpense.route) {
                AddExpenseScreen(
                    onSaveClick = { navController.navigate(Screen.Dashboard.route) },
                    onBackClick  = { navController.popBackStack() }
                )
            }
        }
    }
}
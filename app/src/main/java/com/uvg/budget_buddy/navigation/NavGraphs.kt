package com.uvg.budget_buddy.navigation

import androidx.navigation.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.uvg.budget_buddy.ui.features.login.LoginScreen
import com.uvg.budget_buddy.ui.features.register.RegisterScreen
import com.uvg.budget_buddy.ui.features.onBoarding.OnboardingScreen
import com.uvg.budget_buddy.ui.features.home.DashboardScreen
import com.uvg.budget_buddy.ui.features.home.DashboardViewModel
import com.uvg.budget_buddy.ui.features.addInCome.AddIncomeScreen
import com.uvg.budget_buddy.ui.features.addInCome.AddIncomeViewModel
import com.uvg.budget_buddy.ui.features.addExpense.AddExpenseScreen
import com.uvg.budget_buddy.ui.features.addExpense.AddExpenseViewModel
import com.uvg.budget_buddy.ui.features.profile.ProfileScreen
import com.uvg.budget_buddy.ui.features.settings.SettingsScreen
import com.uvg.budget_buddy.ui.features.settings.SettingsViewModel
import com.uvg.budget_buddy.ui.features.transactionDetail.TransactionDetailScreen

/** Graph de autenticación */
fun NavGraphBuilder.authGraph(nav: NavHostController) {
    navigation(startDestination = Screen.Login.route, route = "auth") {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    nav.navigate(Screen.Onboarding.route)
                },
                onRegisterClick = { nav.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterClick = { nav.popBackStack() },
                onBackClick = { nav.popBackStack() }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onStartClick = {
                    nav.navigate("app") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
    }
}

/** Graph principal de la app con rutas tipadas */
fun NavGraphBuilder.appGraph(
    nav: NavHostController,
    dashboardVm: DashboardViewModel,
    addIncomeVm: AddIncomeViewModel,
    addExpenseVm: AddExpenseViewModel,
    settingsVm: SettingsViewModel,
    isDark: Boolean,
    onToggleDark: (Boolean) -> Unit
) {
    navigation(startDestination = Screen.Dashboard.route, route = "app") {

        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                stateFlow = dashboardVm.state,
                onAddIncomeClick = { nav.navigate(Screen.AddIncome.route) },
                onAddExpenseClick = { nav.navigate(Screen.AddExpense.route) },
                onOpenTxDetail = { id ->
                    nav.navigate("${Screen.TransactionDetail.route}/$id")
                }
            )
        }

        composable(Screen.AddIncome.route) {
            AddIncomeScreen(
                state = addIncomeVm.state,
                onEvent = addIncomeVm::onEvent,
                onBackClick = { nav.popBackStack() },
                onSaved = {  }
            )
        }

        composable(Screen.AddExpense.route) {
            AddExpenseScreen(
                state = addExpenseVm.state,
                onEvent = addExpenseVm::onEvent,
                onBackClick = { nav.popBackStack() },
                onSaved = {  }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = { nav.popBackStack() },
                onEditProfile = { /* TODO */ }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { nav.popBackStack() },
                onLogout = {
                    nav.navigate("auth") {
                        popUpTo("app") { inclusive = true }
                    }
                },
                simulateErrors = settingsVm.simulateErrors,
                onToggleSimulateErrors = settingsVm::toggleSimulateErrors,
                currentDarkMode = isDark,
                onToggleDarkMode = onToggleDark
            )
        }

        // nueva ruta con argumento tipado
        composable(
            route = "${Screen.TransactionDetail.route}/{txId}",
            arguments = listOf(
                navArgument("txId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val txId = backStackEntry.arguments?.getLong("txId") ?: -1L
            TransactionDetailScreen(
                transactionId = txId,
                dashboardVm = dashboardVm,
                onBackClick = { nav.popBackStack() }
            )
        }
    }
}
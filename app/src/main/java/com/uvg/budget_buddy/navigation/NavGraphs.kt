package com.uvg.budget_buddy.navigation

import androidx.navigation.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.uvg.budget_buddy.ui.features.login.LoginScreen
import com.uvg.budget_buddy.ui.features.login.LoginViewModel
import com.uvg.budget_buddy.ui.features.register.RegisterScreen
import com.uvg.budget_buddy.ui.features.register.RegisterViewModel
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
fun NavGraphBuilder.authGraph(
    nav: NavHostController,
    loginVm: LoginViewModel,
    registerVm: RegisterViewModel
) {
    navigation(startDestination = Screen.Login.route, route = "auth") {

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginVm,
                onLoginSuccess = {
                    nav.navigate("app") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    nav.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = registerVm,
                onRegisterSuccess = {
                    nav.navigate("app") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onBackClick = {
                    nav.popBackStack()
                }
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

/** Graph principal de la app */
fun NavGraphBuilder.appGraph(
    nav: NavHostController,
    dashboardVm: DashboardViewModel,
    addIncomeVm: AddIncomeViewModel,
    addExpenseVm: AddExpenseViewModel,
    settingsVm: SettingsViewModel,
    profileVm: com.uvg.budget_buddy.ui.features.profile.ProfileViewModel,  // ← AGREGADO
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
                    nav.navigate("transaction_detail/$id")
                }
            )
        }

        composable(Screen.AddIncome.route) {
            AddIncomeScreen(
                state = addIncomeVm.state,
                onEvent = addIncomeVm::onEvent,
                onBackClick = { nav.popBackStack() },
                onSaved = { }
            )
        }

        composable(Screen.AddExpense.route) {
            AddExpenseScreen(
                state = addExpenseVm.state,
                onEvent = addExpenseVm::onEvent,
                onBackClick = { nav.popBackStack() },
                onSaved = { }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = profileVm,  // ← AGREGADO
                onBackClick = { nav.popBackStack() },
                onEditProfile = { }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsVm,
                onBackClick = { nav.popBackStack() },
                onLogout = {
                    nav.navigate("auth") {
                        popUpTo("app") { inclusive = true }
                    }
                },
                currentDarkMode = isDark,
                onToggleDarkMode = onToggleDark
            )
        }

        // NUEVA RUTA: Detalle de transacción con argumento
        composable(
            route = "transaction_detail/{transactionId}",
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: 0L

            TransactionDetailScreen(
                transactionId = transactionId,
                dashboardVm = dashboardVm,
                onBackClick = { nav.popBackStack() }
            )
        }
    }
}
package com.uvg.budget_buddy.navigation

sealed class Screen(val route: String) {
    // Auth
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Onboarding : Screen("onboarding")

    // App
    data object Dashboard : Screen("dashboard")
    data object AddIncome : Screen("add_income")
    data object AddExpense : Screen("add_expense")
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")

    data object ChangePassword : Screen("change_password")


    //  se implementa esta nueva ruta:  es la de Detalle con argumento
    data object TransactionDetail : Screen("transaction_detail")
}
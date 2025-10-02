package com.uvg.budget_buddy.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Onboarding : Screen("onboarding")

    // Tabs (con BottomNav)
    data object Dashboard : Screen("dashboard")
    data object AddIncome : Screen("add_income")
    data object AddExpense : Screen("add_expense")
}
package com.uvg.budget_buddy.navigation
//La funcion principal es  que define todas las rutas de navegación de la app
// en donde Cada objeto representa una pantalla de la app
sealed class Screen(val route: String) { //para el conjunto de pantallas de la app limitado
    // Pantallas de autenticación
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Onboarding : Screen("onboarding")

    // Pantallas principales con BottomNav (tabs)
    data object Dashboard : Screen("dashboard")
    data object AddIncome : Screen("add_income")
    data object AddExpense : Screen("add_expense")

    // Pantallas del Drawer (menú lateral)
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
    data object Categories : Screen("categories")
    data object Reports : Screen("reports")
}
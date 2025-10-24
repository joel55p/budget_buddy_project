package com.uvg.budget_buddy.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.uvg.budget_buddy.data.repo.FakeBudgetRepository
import com.uvg.budget_buddy.ui.components.BottomNavigation
import com.uvg.budget_buddy.ui.components.AppDrawer
import com.uvg.budget_buddy.ui.features.home.DashboardViewModel
import com.uvg.budget_buddy.ui.features.addInCome.AddIncomeViewModel
import com.uvg.budget_buddy.ui.features.addExpense.AddExpenseViewModel
import com.uvg.budget_buddy.ui.features.settings.SettingsViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.composable
import com.uvg.budget_buddy.ui.features.onBoarding.OnboardingScreen
import com.uvg.budget_buddy.ui.theme.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetBuddyApp(themeVm: ThemeViewModel) {
    val nav = rememberNavController()
    val backEntry by nav.currentBackStackEntryAsState()
    val route = backEntry?.destination?.route
    val tabRoutes = listOf(Screen.Dashboard.route, Screen.AddIncome.route, Screen.AddExpense.route)
    val repo = remember { FakeBudgetRepository()
    }
    val dashboardVm: DashboardViewModel = viewModel(
        factory = viewModelFactory { initializer { DashboardViewModel(repo) } }
    )
    val addIncomeVm: AddIncomeViewModel = viewModel(
        factory = viewModelFactory { initializer { AddIncomeViewModel(repo) } }
    )
    val addExpenseVm: AddExpenseViewModel = viewModel(
        factory = viewModelFactory { initializer { AddExpenseViewModel(repo) } }
    )
    val settingsVm: SettingsViewModel = viewModel(
        factory = viewModelFactory { initializer { SettingsViewModel(repo) } }
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isDark by themeVm.isDarkMode.collectAsState()   // ← lee estado actual

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                onProfile = { scope.launch { drawerState.close() }; nav.navigate(Screen.Profile.route) },
                onSettings = { scope.launch { drawerState.close() }; nav.navigate(Screen.Settings.route) },
                onLogout = {
                    scope.launch { drawerState.close() }
                    nav.navigate("auth") { popUpTo("app") { inclusive = true } }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                if (route in tabRoutes) {
                    TopAppBar(
                        title = {
                            Text(
                                when (route) {
                                    Screen.Dashboard.route -> "BudgetBuddy"
                                    Screen.AddIncome.route -> "Añadir Ingreso"
                                    Screen.AddExpense.route -> "Agregar Gasto"
                                    else -> ""
                                }
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menú")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (route in tabRoutes) {
                    BottomNavigation(
                        currentScreen = when (route) {
                            Screen.Dashboard.route -> "dashboard"
                            Screen.AddIncome.route -> "ingreso"
                            Screen.AddExpense.route -> "gasto"
                            else -> ""
                        },
                        onHomeClick = { nav.navigate(Screen.Dashboard.route) },
                        onAddIncomeClick = { nav.navigate(Screen.AddIncome.route) },
                        onAddExpenseClick = { nav.navigate(Screen.AddExpense.route) }
                    )
                }
            }
        ) { inner ->
            NavHost(
                navController = nav,
                startDestination = "auth",
                modifier = Modifier.padding(inner)
            ) {
                authGraph(nav)
                appGraph(
                    nav = nav,
                    dashboardVm = dashboardVm,
                    addIncomeVm = addIncomeVm,
                    addExpenseVm = addExpenseVm,
                    settingsVm = settingsVm,
                    isDark = isDark,
                    onToggleDark = themeVm::setTheme // se cambio ya que me estaba dando error con el modo oscuro

                )
            }
        }
    }
}
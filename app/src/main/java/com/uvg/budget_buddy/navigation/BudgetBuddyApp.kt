package com.uvg.budget_buddy.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.uvg.budget_buddy.ui.components.BottomNavigation
import com.uvg.budget_buddy.ui.features.login.LoginScreen
import com.uvg.budget_buddy.ui.features.register.RegisterScreen
import com.uvg.budget_buddy.ui.features.onBoarding.OnboardingScreen
import com.uvg.budget_buddy.ui.features.home.DashboardScreen
import com.uvg.budget_buddy.ui.features.addInCome.AddIncomeScreen
import com.uvg.budget_buddy.ui.features.addExpense.AddExpenseScreen
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.unit.dp

private fun NavController.goToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetBuddyApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Pantallas donde se muestran Header + BottomBar
    val tabRoutes = listOf(
        Screen.Dashboard.route,
        Screen.AddIncome.route,
        Screen.AddExpense.route
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Drawer por defecto (izquierda)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)

                NavigationDrawerItem(
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Configuración") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Categorías") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Reportes") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )

                Divider()
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (currentRoute in tabRoutes) {
                    TopAppBar(
                        title = {
                            Text(
                                when (currentRoute) {
                                    Screen.Dashboard.route -> "BudgetBuddy"
                                    Screen.AddIncome.route -> "Añadir Ingreso"
                                    Screen.AddExpense.route -> "Agregar Gasto"
                                    else -> ""
                                }
                            )
                        },
                        // Botón de menú a la IZQUIERDA
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menú")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            },
            bottomBar = {
                if (currentRoute in tabRoutes) {
                    BottomNavigation(
                        currentScreen = when (currentRoute) {
                            Screen.Dashboard.route -> "dashboard"
                            Screen.AddIncome.route -> "ingreso"
                            Screen.AddExpense.route -> "gasto"
                            else -> ""
                        },
                        onHomeClick = {
                            navController.goToTab(Screen.Dashboard.route)
                        },
                        onAddIncomeClick = {
                            navController.goToTab(Screen.AddIncome.route)
                        },
                        onAddExpenseClick = {
                            navController.goToTab(Screen.AddExpense.route)
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Login.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                // LOGIN
                composable(Screen.Login.route) {
                    LoginScreen(
                        onLoginClick = {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onRegisterClick = { navController.navigate(Screen.Register.route) }
                    )
                }

                // REGISTER -> vuelve a login
                composable(Screen.Register.route) {
                    RegisterScreen(
                        onRegisterClick = { navController.popBackStack() },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // ONBOARDING -> DASHBOARD
                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        onStartClick = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }

                // TABS
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        onAddIncomeClick = {
                            navController.navigate(Screen.AddIncome.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onAddExpenseClick = {
                            navController.navigate(Screen.AddExpense.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }

                composable(Screen.AddIncome.route) {
                    AddIncomeScreen(
                        onSaveClick = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Screen.AddExpense.route) {
                    AddExpenseScreen(
                        onSaveClick = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
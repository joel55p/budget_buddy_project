package com.uvg.budget_buddy.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
import com.uvg.budget_buddy.ui.features.profile.ProfileScreen
import com.uvg.budget_buddy.ui.features.settings.SettingsScreen
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp

// el proposito de esta clase es de actuar como controlador principal de navegación y estructura de la app
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetBuddyApp() { //es la que se llamara en el main activity
    // NavController: Maneja la navegación entre pantallas
    val navController = rememberNavController()
    // Observa la pantalla actual en el stack de navegación
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val tabRoutes = listOf( // Define qué pantallas muestran TopBar y BottomBar
        Screen.Dashboard.route,
        Screen.AddIncome.route,
        Screen.AddExpense.route
    )
// Estado del drawer (menú lateral)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()// Para operaciones asíncronas

    // ModalNavigationDrawer: Menú lateral deslizable desde la izquierda
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Menú",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                // Cada item del drawer navega a su pantalla
                NavigationDrawerItem(
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()// Cierra el drawer
                            navController.navigate(Screen.Profile.route) // Navega a la pantalla de perfil
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Configuración") }, //para settings
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.Settings.route)
                        }
                    }
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

                HorizontalDivider() // Separador horizontal

                // Item de cerrar sesión
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    ) {
        Scaffold( // Estructura principal de la pantalla que incluye TopBar, BottomBar y contenido
            topBar = {
                if (currentRoute in tabRoutes) {
                    // Solo muestra TopBar en las pantallas de tabs
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

                        navigationIcon = {
                            // Botón de menú hamburguesa
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
                // Solo muestra BottomBar en las pantallas de tabs
                if (currentRoute in tabRoutes) {
                    BottomNavigation(
                        currentScreen = when (currentRoute) {
                            Screen.Dashboard.route -> "dashboard"
                            Screen.AddIncome.route -> "ingreso"
                            Screen.AddExpense.route -> "gasto"
                            else -> ""
                        },
                        onHomeClick = { //navega a dashboard
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onAddIncomeClick = {//navega a add income
                            navController.navigate(Screen.AddIncome.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onAddExpenseClick = { //navega a add expense
                            navController.navigate(Screen.AddExpense.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            // NavHost: Contenedor que cambia de pantalla según la ruta
            NavHost(
                navController = navController,
                startDestination = Screen.Login.route, //sera la primera pantalla
                modifier = Modifier.padding(innerPadding)
            ) {
                // Define cada pantalla y su comportamiento
                composable(Screen.Login.route) {
                    LoginScreen(
                        onLoginClick = {
                            // Al hacer login, va a Onboarding y elimina Login del stack
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onRegisterClick = { navController.navigate(Screen.Register.route) }
                    )
                }

                composable(Screen.Register.route) {
                    RegisterScreen(
                        onRegisterClick = { navController.popBackStack() },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        onStartClick = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        onAddIncomeClick = {
                            navController.navigate(Screen.AddIncome.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onAddExpenseClick = {
                            navController.navigate(Screen.AddExpense.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
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
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
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
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onBackClick = { navController.popBackStack() },
                        onEditProfile = { } /* Placeholder */
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onBackClick = { navController.popBackStack() },
                        onLogout = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
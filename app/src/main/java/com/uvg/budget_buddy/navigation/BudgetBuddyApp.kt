package com.uvg.budget_buddy.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.database.FirebaseDatabase
import com.uvg.budget_buddy.data.local.AppDatabase
import com.uvg.budget_buddy.data.local.preferences.UserPreferencesDataStore
import com.uvg.budget_buddy.data.repo.AuthRepository
import com.uvg.budget_buddy.data.repo.FirebaseBudgetRepository
import com.uvg.budget_buddy.ui.components.BottomNavigation
import com.uvg.budget_buddy.ui.components.AppDrawer
import com.uvg.budget_buddy.ui.features.home.DashboardViewModel
import com.uvg.budget_buddy.ui.features.addInCome.AddIncomeViewModel
import com.uvg.budget_buddy.ui.features.addExpense.AddExpenseViewModel
import com.uvg.budget_buddy.ui.features.settings.SettingsViewModel
import com.uvg.budget_buddy.ui.features.login.LoginViewModel
import com.uvg.budget_buddy.ui.features.register.RegisterViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import com.uvg.budget_buddy.ui.theme.ThemeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetBuddyApp(themeVm: ThemeViewModel) {
    val context = LocalContext.current
    val nav = rememberNavController()
    val backEntry by nav.currentBackStackEntryAsState()
    val route = backEntry?.destination?.route
    val tabRoutes = listOf(Screen.Dashboard.route, Screen.AddIncome.route, Screen.AddExpense.route)

    // Inicializar dependencias
    val database = remember { AppDatabase.getInstance(context) }
    val userPreferences = remember { UserPreferencesDataStore(context) }
    val authRepository = remember { AuthRepository(userPreferences = userPreferences) }
    val firebaseDb = remember { FirebaseDatabase.getInstance() }
    val budgetRepo = remember {
        FirebaseBudgetRepository(firebaseDb, database.transactionDao(), authRepository)
    }

    // ViewModels
    val loginVm: LoginViewModel = viewModel(
        factory = viewModelFactory {
            initializer { LoginViewModel(authRepository) }
        }
    )

    val registerVm: RegisterViewModel = viewModel(
        factory = viewModelFactory {
            initializer { RegisterViewModel(authRepository) }
        }
    )

    val dashboardVm: DashboardViewModel = viewModel(
        factory = viewModelFactory {
            initializer { DashboardViewModel(budgetRepo) }
        }
    )

    val addIncomeVm: AddIncomeViewModel = viewModel(
        factory = viewModelFactory {
            initializer { AddIncomeViewModel(budgetRepo) }
        }
    )

    val addExpenseVm: AddExpenseViewModel = viewModel(
        factory = viewModelFactory {
            initializer { AddExpenseViewModel(budgetRepo) }
        }
    )

    val settingsVm: SettingsViewModel = viewModel(
        factory = viewModelFactory {
            initializer { SettingsViewModel(budgetRepo, authRepository, userPreferences) }
        }
    )

    // ← AGREGADO ProfileViewModel
    val profileVm: com.uvg.budget_buddy.ui.features.profile.ProfileViewModel = viewModel(
        factory = viewModelFactory {
            initializer { com.uvg.budget_buddy.ui.features.profile.ProfileViewModel(authRepository) }
        }
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isDark by themeVm.isDarkMode.collectAsState()

    // Observar estado de autenticación desde Firebase directamente
    val authState by authRepository.observeAuthState().collectAsState(initial = null)
    val isLoggedIn = authState != null

    // Estado para controlar la inicialización
    var isInitialized by remember { mutableStateOf(false) }

    // Inicializar después de un pequeño delay
    LaunchedEffect(Unit) {
        delay(100)
        isInitialized = true
    }

    // Navegar automáticamente según el estado de autenticación
    LaunchedEffect(isLoggedIn, route, isInitialized) {
        if (!isInitialized) return@LaunchedEffect

        if (isLoggedIn) {
            // Usuario autenticado
            if (route == Screen.Login.route ||
                route == Screen.Register.route ||
                route == "auth" ||
                route == null) {
                delay(200) // Pequeño delay para asegurar que Firebase está listo
                nav.navigate("app") {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else {
            // Usuario no autenticado
            if (route != Screen.Login.route &&
                route != Screen.Register.route &&
                route != "auth" &&
                route != null) {
                nav.navigate("auth") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                onProfile = {
                    scope.launch { drawerState.close() }
                    nav.navigate(Screen.Profile.route)
                },
                onSettings = {
                    scope.launch { drawerState.close() }
                    nav.navigate(Screen.Settings.route)
                },
                onLogout = {
                    scope.launch {
                        drawerState.close()
                        settingsVm.logout()
                    }
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
                startDestination = if (isLoggedIn) "app" else "auth",
                modifier = Modifier.padding(inner)
            ) {
                authGraph(
                    nav = nav,
                    loginVm = loginVm,
                    registerVm = registerVm
                )
                appGraph(
                    nav = nav,
                    dashboardVm = dashboardVm,
                    addIncomeVm = addIncomeVm,
                    addExpenseVm = addExpenseVm,
                    settingsVm = settingsVm,
                    profileVm = profileVm,  // ← AGREGADO
                    isDark = isDark,
                    onToggleDark = themeVm::setTheme
                )
            }
        }
    }
}
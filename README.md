Descripción
BudgetBuddy es una aplicación Android moderna para gestión de finanzas personales, desarrollada con Jetpack Compose y siguiendo las mejores prácticas de arquitectura MVVM.

Patrón MVVM (Model-View-ViewModel):
┌─────────────────────────────────────────┐
│              View (UI)                   │
│  ┌─────────────────────────────────┐   │
│  │   Composables (Screens)         │   │
│  │   - State hoisting              │   │
│  │   - Pure functions              │   │
│  └─────────────┬───────────────────┘   │
│                │ collectAsStateWithLifecycle
│                ▼                        │
│  ┌─────────────────────────────────┐   │
│  │      ViewModel                   │   │
│  │  - UiState (immutable)          │   │
│  │  - UiEvents                     │   │
│  │  - StateFlow                    │   │
│  └─────────────┬───────────────────┘   │
│                │ suspend calls          │
│                ▼                        │
│  ┌─────────────────────────────────┐   │
│  │      Repository                  │   │
│  │  - Flow<Resource<T>>            │   │
│  │  - Abstracción de datos         │   │
│  └─────────────┬───────────────────┘   │
│                │                        │
│                ▼                        │
│  ┌─────────────────────────────────┐   │
│  │    Data Source (Fake)           │   │
│  │  - Simula latencia (delay)      │   │
│  │  - Errores simulados            │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘

Estructura del Proyecto: 


app/src/main/java/com/uvg/budget_buddy/
├── data/
│   ├── model/
│   │   └── Transaction.kt              # Modelo de datos
│   └── repo/
│       ├── BudgetRepository.kt         # Interfaz del repositorio
│       ├── FakeBudgetRepository.kt     # Implementación fake con delay/errores
│       └── Resource.kt                 # Sealed class para estados
├── navigation/
│   ├── BudgetBuddyApp.kt              # Configuración principal de navegación
│   ├── NavGraphs.kt                    # Nested graphs (auth, app)
│   └── Screens.kt                      # Definición de rutas
├── ui/
│   ├── components/                     # Componentes reutilizables
│   │   ├── AppDrawer.kt
│   │   ├── BottomNavigation.kt
│   │   └── HeaderBar.kt
│   ├── features/                       # Pantallas por feature
│   │   ├── addExpense/
│   │   │   ├── AddExpenseScreen.kt
│   │   │   └── AddExpenseViewModel.kt
│   │   ├── addInCome/
│   │   │   ├── AddIncomeScreen.kt
│   │   │   └── AddIncomeViewModel.kt
│   │   ├── home/
│   │   │   ├── DashboardScreen.kt
│   │   │   └── DashboardViewModel.kt
│   │   ├── login/
│   │   │   └── LoginScreen.kt
│   │   ├── onBoarding/
│   │   │   └── OnboardingScreen.kt
│   │   ├── profile/
│   │   │   └── ProfileScreen.kt
│   │   ├── register/
│   │   │   └── RegisterScreen.kt
│   │   ├── settings/
│   │   │   ├── SettingsScreen.kt
│   │   │   └── SettingsViewModel.kt
│   │   └── transactionDetail/
│   │       └── TransactionDetailScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       ├── ThemeViewModel.kt
│       └── Type.kt
└── MainActivity.kt



Flujo de Navegación: 
Graph de Autenticación:


Login ──→ Register
  │         │
  │         ↓
  │       (Back)
  ↓
Onboarding ──→ App Graph

Graph Principal (App):


Dashboard ←──┬──→ Add Income
  │          │
  ├──→ Add Expense
  ├──→ Transaction Detail (con argumento ID)
  ├──→ Profile
  └──→ Settings ──→ Logout → Auth Graph




  Características Técnicas
1. Navegación

Nested Navigation Graphs: Separación auth/app
Argumentos Tipados: TransactionDetail recibe Long ID
Back Stack Management: popUpTo con inclusive para evitar regresos indeseados
Safe Args: Validación de argumentos nullable

2. State Management

StateFlow: Flujos reactivos de estado
collectAsStateWithLifecycle: Manejo automático del lifecycle
UiState Inmutable: Data classes con val
Single Source of Truth: Estado unidireccional

3. Asincronía

Coroutines: Operaciones asíncronas con suspend
Flow: Streams reactivos para observar datos
Resource Pattern: Loading → Success/Error
Delay Simulation: 400ms-800ms para realismo

4. Manejo de Errores
kotlinsealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}

Simulación de Errores: 
La app incluye un repositorio fake que simula:

 1.Latencia de red (delay)
2. Errores aleatorios (1 de cada 4 requests)
3. Estados de carga
4. Recuperación de errores

Cómo activar:

Dashboard → Menú → Configuración
Activar "Simular errores"
Intentar agregar ingresos/gastos

Temas y Estilos

-Material 3 Design
-Modo claro/oscuro (persistente en ViewModel)
-Colores personalizados:

1.Verde principal (#6BCB77)
2.Rojo suave (#FF6B6B) para gastos
3.Verde suave (#51CF66) para ingresos
4.Azul suave (#4DABF7) para balance



QUINTA ENTREGA

se mantuvo la siguiente estructura 
pp/src/main/java/com/uvg/budget_buddy/
├── data/
│   ├── local/                     # Room y DataStore
│   │   ├── dao/Tdao.kt
│   │   ├── entity/Tentity.kt
│   │   ├── preferences/UserPreferencesDataStore.kt
│   │   └── AppDatabase.kt
│   ├── model/
│   │   └── Transaction.kt
│   └── repo/
│       ├── AuthRepository.kt
│       ├── BudgetRepository.kt      # Interfaz
│       ├── FakeBudgetRepository.kt  # Implementación fake
│       ├── FirebaseBudgetRepository.kt # Implementación real (NUEVO)
│       └── Resource.kt
├── navigation/
│   ├── BudgetBuddyApp.kt
│   ├── NavGraphs.kt
│   └── Screens.kt
├── ui/
│   ├── components/
│   ├── features/                  # Pantallas por feature (MVVM)
│   │   ├── addExpense/
│   │   ├── addInCome/
│   │   ├── home/
│   │   ├── login/
│   │   ├── profile/
│   │   ├── register/
│   │   ├── settings/
│   │   └── transactionDetail/
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       ├── ThemeViewModel.kt
│       └── Type.kt
└── MainActivity.kt


ademas se tienen los siguientes flujo 
 1.Flujo de Lectura (Cache-first, then Network):

La UI (p.ej. DashboardScreen) observa el Flow del DashboardViewModel.

El ViewModel consume el Flow<Resource<List<Transaction>>> del repositorio.

El FirebaseBudgetRepository emite primero los datos locales de Room para una carga instantánea (.onStart).

Simultáneamente, establece un ValueEventListener de Firebase.

Cuando Firebase devuelve datos (onDataChange), el repositorio los guarda en Room (transactionDao.insertTransaction).

Como Room devuelve un Flow, la base de datos local emite automáticamente los nuevos datos, actualizando la UI de forma reactiva.

Si Firebase falla, el Flow entra en el .catch y emite los datos de Room como fallback.

2. Flujo de Escritura (Offline-first):

El usuario agrega un ingreso o gasto (p.ej. AddIncomeViewModel).

El ViewModel llama a repo.addIncome().

El FirebaseBudgetRepository guarda la transacción en Room primero, marcándola como no sincronizada (syncedWithFirebase = false).

La UI se actualiza inmediatamente (gracias al Flow de Room).

El repositorio intenta sincronizar el dato con Firebase.

Si tiene éxito, actualiza el registro en Room a syncedWithFirebase = true.

Si falla (sin conexión), la transacción permanece en Room como pendiente para una sincronización futura (syncPendingTransactions).

Manejo de Estados (Carga y Error): Se utiliza la clase Resource<T> (Loading, Success, Error) para comunicar el estado de la carga de datos del repositorio al ViewModel y, finalmente, a la UI, mostrando CircularProgressIndicator o mensajes de error.




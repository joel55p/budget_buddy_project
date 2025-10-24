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



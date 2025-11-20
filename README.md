<img width="387" height="572" alt="image" src="https://github.com/user-attachments/assets/b9fe93cf-56f5-49fe-aa58-a50dacb1517b" />
## Descripción

BudgetBuddy es una aplicación Android moderna diseñada para la *gestión personal de finanzas, desarrollada con **Jetpack Compose* y siguiendo estrictamente las mejores prácticas de la arquitectura *MVVM (Model-View-ViewModel). Esta aplicación ofrece una experiencia de usuario fluida y reactiva, con funcionalidades robustas de persistencia de datos local y sincronización en la nube mediante **Firebase*.

### Patrón MVVM (Model-View-ViewModel)

Nuestra arquitectura se adhiere firmemente al patrón MVVM, asegurando una clara separación de responsabilidades, facilidad de prueba y mantenimiento.


┌─────────────────────────────────────────┐ │ View (UI) │ │ ┌─────────────────────────────────┐ │ │ │ Composables (Screens) │ │ │ │ - State hoisting │ │ │ │ - Pure functions │ │ │ └─────────────┬───────────────────┘ │ │ │ collectAsStateWithLifecycle │ ▼ │ │ ┌─────────────────────────────────┐ │ │ │ ViewModel │ │ │ │ - UiState (immutable) │ │ │ │ - UiEvents │ │ │ │ - StateFlow │ │ │ └─────────────┬───────────────────┘ │ │ │ suspend calls │ │ ▼ │ │ ┌─────────────────────────────────┐ │ │ │ Repository │ │ │ │ - Flow<Resource<T>> │ │ │ │ - Abstracción de datos │ │ │ └─────────────┬───────────────────┘ │ │ │ │ │ ▼ │ │ ┌─────────────────────────────────┐ │ │ │ Data Source (Local & Remote) │ │ │ │ - Room Database │ │ │ │ - Firebase Realtime DB │ │ │ │ - Firebase Auth │ │ │ └─────────────────────────────────┘ │ └─────────────────────────────────────────┘



## Estructura del Proyecto

La organización del código sigue una estructura modular y coherente, facilitando la navegación y el mantenimiento del proyecto.

app/src/main/java/com/uvg/budget_buddy/ ├── data/ │ ├── local/ # Persistencia local con Room y DataStore │ │ ├── dao/TransactionDao.kt # Data Access Object para Room │ │ ├── entity/TransactionEntity.kt # Entidad de la base de datos Room │ │ ├── preferences/UserPreferencesDataStore.kt # Para preferencias de usuario (ej. tema) │ │ └── AppDatabase.kt # Base de datos Room │ ├── model/ │ │ └── Transaction.kt # Modelo de datos principal │ └── repo/ │ ├── AuthRepository.kt # Repositorio para autenticación (Firebase Auth) │ ├── BudgetRepository.kt # Interfaz del repositorio de transacciones │ ├── FakeBudgetRepository.kt # Implementación fake para pruebas/simulación │ ├── FirebaseBudgetRepository.kt # Implementación real con Firebase Realtime DB (¡NUEVO!) │ └── Resource.kt # Sealed class para gestionar estados de UI (Loading, Success, Error) ├── navigation/ │ ├── BudgetBuddyApp.kt # Configuración principal del NavHost │ ├── NavGraphs.kt # Definición de grafos de navegación (Auth, App) │ └── Screens.kt # Definición de rutas y argumentos de navegación ├── ui/ │ ├── components/ # Componentes de UI reutilizables │ │ ├── AppDrawer.kt │ │ ├── BottomNavigation.kt │ │ └── HeaderBar.kt │ ├── features/ # Pantallas de la aplicación organizadas por feature (MVVM) │ │ ├── addExpense/ │ │ │ ├── AddExpenseScreen.kt │ │ │ └── AddExpenseViewModel.kt │ │ ├── addInCome/ │ │ │ ├── AddIncomeScreen.kt │ │ │ └── AddIncomeViewModel.kt │ │ ├── home/ │ │ │ ├── DashboardScreen.kt │ │ │ └── DashboardViewModel.kt │ │ ├── login/ │ │ │ ├── LoginScreen.kt │ │ │ └── LoginViewModel.kt # Añadido ViewModel si aplica │ │ ├── onboarding/ │ │ │ └── OnboardingScreen.kt │ │ ├── profile/ │ │ │ ├── ProfileScreen.kt │ │ │ └── ProfileViewModel.kt # Añadido ViewModel si aplica │ │ ├── register/ │ │ │ ├── RegisterScreen.kt │ │ │ └── RegisterViewModel.kt # Añadido ViewModel si aplica │ │ ├── settings/ │ │ │ ├── SettingsScreen.kt │ │ │ └── SettingsViewModel.kt │ │ └── transactionDetail/ │ │ ├── TransactionDetailScreen.kt │ │ └── TransactionDetailViewModel.kt # Añadido ViewModel si aplica │ └── theme/ # Definición de temas, colores y tipografías │ ├── Color.kt │ ├── Theme.kt │ ├── ThemeViewModel.kt # Para gestionar el tema dinámicamente │ └── Type.kt └── MainActivity.kt # Actividad principal de la aplicación


## Flujo de Navegación

La aplicación utiliza la librería Jetpack Navigation para gestionar las transiciones entre pantallas, organizadas en grafos de navegación anidados.

### Graph de Autenticación (AuthGraph)

Gestiona el flujo de usuarios no logueados.

Login ───→ Register │ │ │ ↓ (Back) │ (Back) ↓ Onboarding ──→ App Graph


### Graph Principal (AppGraph)

Contiene la funcionalidad principal una vez que el usuario ha iniciado sesión.

Dashboard ←──┬──→ Add Income │ │ ├──→ Add Expense ├──→ Transaction Detail (con argumento ID) ├──→ Profile └──→ Settings ──→ Logout → Auth Graph


### 2. State Management
* *StateFlow:* Flujos reactivos para la emisión del estado de la UI desde los ViewModels.
* *collectAsStateWithLifecycle:* Recolección de estados de forma consciente del ciclo de vida del componente Compose.
* *UiState Inmutable:* Uso de data classes con propiedades val para garantizar la inmutabilidad de los estados de la UI.
* *Single Source of Truth:* El estado de la aplicación se gestiona desde una única fuente, garantizando coherencia.

### 3. Asincronía y Persistencia Offline-First
* *Coroutines:* Uso de suspend functions para operaciones asíncronas, como acceso a la base de datos y llamadas a la API de Firebase.
* *Flow:* Streams reactivos para observar cambios en los datos locales (Room) y remotos (Firebase).
* *Resource Pattern:* Utilización de sealed class Resource<T> (Loading, Success, Error) para comunicar el estado de las operaciones de datos a la UI.

### 4. Estrategia Offline-First con Firebase y Room

La aplicación implementa una robusta estrategia "Offline-First", donde la base de datos local (Room) actúa como la principal fuente de datos, y Firebase se encarga de la sincronización en la nube.

#### Flujo de Lectura (Cache-first, then Network)
1.  La *UI* (ej. DashboardScreen) observa el Flow de DashboardViewModel.
2.  El *ViewModel* consume el Flow<Resource<List<Transaction>>> del repositorio.
3.  El FirebaseBudgetRepository emite primero los datos locales de *Room* para una carga instantánea (.onStart).
4.  Simultáneamente, establece un ValueEventListener de *Firebase Realtime Database*.
5.  Cuando Firebase devuelve datos (onDataChange), el repositorio los guarda en *Room* (transactionDao.insertTransaction).
6.  Como Room devuelve un Flow, la base de datos local emite automáticamente los nuevos datos, actualizando la UI de forma reactiva y en tiempo real.
7.  Si la llamada a Firebase falla, el Flow entra en el bloque .catch y emite los datos de Room como fallback.

#### Flujo de Escritura (Offline-first)
1.  El usuario agrega un ingreso o gasto (ej. AddIncomeViewModel).
2.  El *ViewModel* llama a repo.addIncome().
3.  El FirebaseBudgetRepository *guarda la transacción en Room primero*, marcándola como no sincronizada (syncedWithFirebase = false).
4.  La *UI* se actualiza inmediatamente (gracias al Flow de Room).
5.  El repositorio intenta sincronizar el dato con *Firebase*.
6.  Si tiene éxito, actualiza el registro en Room a syncedWithFirebase = true.
7.  Si falla (sin conexión), la transacción permanece en Room como pendiente para una sincronización futura (gestionado por una función syncPendingTransactions() implementada en el repositorio).

### 5. Manejo de Errores y Simulación
* *Manejo de Estados (Carga y Error):* Se utiliza la clase Resource<T> (Loading, Success, Error) para comunicar el estado de la carga de datos del repositorio al ViewModel y, finalmente, a la UI, mostrando CircularProgressIndicator o mensajes de error apropiados.
* *Simulación de Errores:* La aplicación incluye un repositorio fake (FakeBudgetRepository) y lógica en el repositorio real que permite simular:
    1.  Latencia de red (delay) para un comportamiento realista.
    2.  Errores aleatorios (ej. 1 de cada 4 requests fallan).
    3.  Manejo de estados de carga y recuperación de errores.
    * *Cómo activar:* Dashboard → Menú Drawer → Configuración → Activar "Simular errores". Esto permite probar la robustez y resiliencia de la aplicación.

### 6. Temas y Estilos
* *Material 3 Design:* Implementación de la guía de diseño más reciente de Google para una interfaz de usuario moderna.
* *Modo Claro/Oscuro:* La aplicación soporta ambos modos de tema, persistente en el ThemeViewModel y UserPreferencesDataStore.
* *Colores personalizados:* Paleta de colores distintiva:
    * Verde principal (#6BCB77) para elementos clave.
    * Rojo suave (#FF6B6B) para gastos.
    * Verde suave (#51CF66) para ingresos.
    * Azul suave (#4DABF7) para el balance general.

## Requisitos del Sistema

* *Android Studio:* Jellyfish | 2023.3.1 o superior.
* *SDK Mínimo:* API Level 24 (Android 7.0 Nougat).
* *SDK Objetivo:* API Level 34 (Android 14).
* *Lenguaje:* Kotlin.

*Desarrollado por:* Miguel Rosas, Joel Nerio y Samu Robles
*Curso:* Desarrollo de Aplicaciones Móviles - Universidad del Valle de Guatemala
*Fecha de Última Actualización:* 20 de Noviembre de 2025

# 🧠 ROADMAP INICIAL DEL PROYECTO

## 🧱 Definición base (CRÍTICA)

### ✅ Tareas

- Definir nombres de paquetes base:
```
com.doldom.farmacia
```

- Definir módulos lógicos:
  - core
  - data
  - domain
  - presentation

- Definir convención de nombres:
  - View → SomethingView
  - Controller → SomethingController
  - ViewModel → SomethingViewModel
  - Repository → SomethingRepository

- Definir estructura de carpetas:
```
/presentation
   /views
   /components
   /navigation
/domain
   /model
   /usecase
/data
   /repository
   /datasource
   /database
/core
   /di
   /utils
```

---

## 🧠 Arquitectura (NO SALTAR)

### 🎯 Decisión obligatoria
👉 MVVM + Clean Architecture (adaptado a escritorio)

### ✅ Tareas

- Implementar:
  - BaseViewModel
  - BaseController

- Definir flujo:
```
View → ViewModel → UseCase → Repository → DataSource → DB
```

- Crear sistema de navegación:
  - Navigator.kt
  - Manejo de escenas sin recargar Stage

- Crear sistema de estado UI:
  - Loading
  - Error
  - Success

---

## 🗄️ Base de datos

### ✅ Tareas

- Diseñar esquema inicial:
  - productos
  - ventas
  - detalle_ventas
  - compras
  - inventario
  - sucursales
  - usuarios

- Configurar:
  - HikariCP
  - Pool de conexiones

- Crear:
  - DatabaseFactory.kt
  - ConnectionManager.kt

---

## ⚙️ Infraestructura

### ✅ Tareas

- Configurar corrutinas:
  - Dispatcher IO para DB
  - Dispatcher Main para UI

- Crear:
  - CoroutineScope global controlado
  - Manejo de lifecycle

- Logging:
  - SLF4J + Logback

---

## 🧩 Componentes UI reutilizables

### 🎯 Clave para mantener consistencia del DESIGN.md

### ✅ Tareas

- Crear componentes base:
  - Sidebar
  - Topbar
  - CardContainer
  - InputField custom
  - ButtonPrimary / Secondary
  - TableView estilizada

- Implementar:
  - sistema de colores del DESIGN.md
  - sin bordes (NO-LINE RULE)
  - spacing system

---

## 🧭 Navegación

### ✅ Tareas

- Definir rutas:
  - /dashboard
  - /inventario
  - /ventas
  - /compras
  - /caja
  - /reportes

- Crear:
  - Router centralizado
  - Lazy loading de vistas

---

## 🧾 Features principales

### 🔹 Orden recomendado

#### 1. Dashboard
- KPIs
- resumen ventas
- alertas

#### 2. Inventario
- listado productos
- búsqueda dinámica
- edición

#### 3. Ventas (CRÍTICO)
- POS
- carrito
- cálculo en tiempo real

#### 4. Compras
- ingreso stock
- actualización inventario

#### 5. Caja diaria
- cierre
- reporte

---

## 🚧 RESTRICCIONES IMPORTANTES (NO NEGOCIABLES)

### 🧱 Arquitectura

❌ NO hacer:
- lógica en Controllers
- acceso directo a DB desde UI
- queries en ViewModel

✅ SIEMPRE:
- usar UseCases
- separar capas estrictamente

---

### ⚡ Concurrencia

❌ NO:
- bloquear hilo UI (Thread.sleep)
- hacer queries sin corrutinas

✅ SI:
```kotlin
withContext(Dispatchers.IO) {
    repository.getProductos()
}
```

---

### 🗄️ Base de datos

❌ NO:
- conexiones manuales
- abrir/cerrar conexión en cada query

✅ SI:
- usar HikariCP
- prepared statements

---

### 🎨 UI

❌ PROHIBIDO:
- bordes
- colores puros (#000, #FFF)
- layouts rígidos

✅ OBLIGATORIO:
- capas
- spacing
- esquinas redondeadas

---

### 🔁 Estado UI

❌ NO:
- lógica dispersa
- flags booleanos sin control

✅ SI:
```kotlin
sealed class UiState {
    object Loading
    data class Success<T>(val data: T)
    data class Error(val message: String)
}
```

---

### 📦 Código

❌ NO:
- clases gigantes
- métodos > 50 líneas
- lógica duplicada

✅ SI:
- SOLID
- single responsibility
- composición sobre herencia

---

## 🧪 Testing

- Unit test en UseCases
- Test de repositorios con DB mock
- Validación de lógica de negocio

---

## 🧠 DECISIONES CLAVE

1. No es CRUD → Es sistema transaccional tipo POS  
2. Rendimiento crítico → consultas optimizadas, paginación, debounce  
3. UI fluida → animaciones suaves, sin recargas  
4. Escalable → multi-sucursal, FEL, offline futuro  

---

## 🚀 PRIMERAS TAREAS

1. Implementar estructura
2. Configurar HikariCP
3. Configurar Corrutinas kotlin
4. instalar fonts de DESIGN.md y configurarlos para el proyecto
5. Crear DatabaseFactory
6. Crear Navigator
7. Crear BaseViewModel
8. Crear Sidebar
9. Crear DashboardView
10. Primera query

---

## ⚠️ ERRORES CRÍTICOS

- Mezclar UI con SQL
- No usar corrutinas
- No usar pool conexiones
- No centralizar navegación
- Ignorar design system
- Empezar sin base

---

## 💡 RECOMENDACIÓN FINAL

Implementar vertical slice:

```
Dashboard
   ↓
ViewModel
   ↓
UseCase
   ↓
Repository
   ↓
MariaDB
```

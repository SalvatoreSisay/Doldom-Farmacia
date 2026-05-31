package com.doldom.farmacia.presentation.navigation

enum class AppRoute(val fxmlPath: String, val title: String) {
    LOGIN("/com/doldom/farmacia/presentation/views/login/login-view.fxml", "DOLDOM - Inicio de sesion"),
    HOME("/com/doldom/farmacia/presentation/views/home/home-view.fxml", "DOLDOM - Panel principal"),
    INVENTARIO("/com/doldom/farmacia/presentation/views/inventario/inventario-view.fxml", "DOLDOM - Inventario"),
    VENTAS("/com/doldom/farmacia/presentation/views/ventas/ventas-view.fxml", "DOLDOM - Ventas"),
    COMPRAS("/com/doldom/farmacia/presentation/views/compras/compras-view.fxml", "DOLDOM - Compras"),
    CAJA("/com/doldom/farmacia/presentation/views/caja/caja-view.fxml", "DOLDOM - Caja diaria"),
    CALENDARIO("/com/doldom/farmacia/presentation/views/calendario/calendario-view.fxml", "DOLDOM - Calendario"),
    CONTABILIDAD("/com/doldom/farmacia/presentation/views/contabilidad/contabilidad-view.fxml", "DOLDOM - Contabilidad")
}

package com.doldom.farmacia.presentation.navigation

enum class AppRoute(val fxmlPath: String, val title: String) {
    LOGIN("/com/doldom/farmacia/presentation/views/login/login-view.fxml", "DOLDOM - Inicio de sesion"),
    HOME("/com/doldom/farmacia/presentation/views/home/home-view.fxml", "DOLDOM - Panel principal"),
    INVENTARIO("/com/doldom/farmacia/presentation/views/inventario/inventario-view.fxml", "DOLDOM - Inventario")
}

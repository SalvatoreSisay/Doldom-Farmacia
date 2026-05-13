package com.doldom.farmacia.presentation.navigation

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

object Navigator {
    private lateinit var stage: Stage

    fun init(stage: Stage) {
        this.stage = stage
    }

    fun navigateTo(route: AppRoute) {
        check(::stage.isInitialized) { "Navigator no inicializado" }
        val root = load(route.fxmlPath)
        stage.scene = Scene(root, stage.width.takeIf { it > 0 } ?: 1600.0, stage.height.takeIf { it > 0 } ?: 900.0)
        stage.title = route.title
    }

    fun goToHome() = navigateTo(AppRoute.HOME)
    fun goToInventario() = navigateTo(AppRoute.INVENTARIO)
    fun goToLogin() = navigateTo(AppRoute.LOGIN)

    private fun load(path: String): Parent {
        val resource = Navigator::class.java.getResource(path)
            ?: error("No se encontro recurso FXML: $path")
        return FXMLLoader.load(resource)
    }
}

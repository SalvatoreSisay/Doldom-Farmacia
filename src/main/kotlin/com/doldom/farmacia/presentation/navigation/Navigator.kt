package com.doldom.farmacia.presentation.navigation

import javafx.animation.FadeTransition
import javafx.animation.Interpolator
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.util.Duration

object Navigator {
    private lateinit var stage: Stage

    fun init(stage: Stage) {
        this.stage = stage
    }

    fun navigateTo(route: AppRoute) {
        check(::stage.isInitialized) { "Navigator no inicializado" }
        val root = load(route.fxmlPath)
        root.opacity = 0.0
        stage.scene = Scene(root, stage.width.takeIf { it > 0 } ?: 1600.0, stage.height.takeIf { it > 0 } ?: 900.0)
        stage.title = route.title
        FadeTransition(Duration.millis(180.0), root).apply {
            fromValue = 0.0
            toValue = 1.0
            interpolator = Interpolator.EASE_OUT
            play()
        }
    }

    fun goToHome() = navigateTo(AppRoute.HOME)
    fun goToInventario() = navigateTo(AppRoute.INVENTARIO)
    fun goToVentas() = navigateTo(AppRoute.VENTAS)
    fun goToCompras() = navigateTo(AppRoute.COMPRAS)
    fun goToCaja() = navigateTo(AppRoute.CAJA)
    fun goToCalendario() = navigateTo(AppRoute.CALENDARIO)
    fun goToContabilidad() = navigateTo(AppRoute.CONTABILIDAD)
    fun goToLogin() = navigateTo(AppRoute.LOGIN)

    private fun load(path: String): Parent {
        val resource = Navigator::class.java.getResource(path)
            ?: error("No se encontro recurso FXML: $path")
        return FXMLLoader.load(resource)
    }
}

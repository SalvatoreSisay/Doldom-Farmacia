package com.doldom.farmacia.presentation.views.login

import com.doldom.farmacia.presentation.navigation.Navigator
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class LoginView : Application() {
    override fun start(stage: Stage) {
        Navigator.init(stage)
        val root = FXMLLoader.load<javafx.scene.Parent>(
            LoginView::class.java.getResource("/com/doldom/farmacia/presentation/views/login/login-view.fxml")
        )
        val scene = Scene(root, 1600.0, 900.0)
        stage.title = "DOLDOM - Inicio de sesión"
        stage.scene = scene
        stage.minWidth = 1366.0
        stage.minHeight = 768.0
        stage.isResizable = true
        stage.show()
    }
}

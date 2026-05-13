package com.doldom.farmacia.presentation.views.login

import com.doldom.farmacia.presentation.navigation.Navigator
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.shape.Rectangle

class LoginController {
    @FXML
    private lateinit var cardContainer: HBox

    @FXML
    private lateinit var userField: TextField

    @FXML
    private lateinit var passwordField: PasswordField

    @FXML
    private lateinit var passwordVisibleField: TextField

    @FXML
    private fun initialize() {
        passwordVisibleField.isManaged = false
        passwordVisibleField.isVisible = false
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty())

        val clip = Rectangle()
        clip.arcWidth = 96.0
        clip.arcHeight = 96.0
        clip.widthProperty().bind(cardContainer.widthProperty())
        clip.heightProperty().bind(cardContainer.heightProperty())
        cardContainer.clip = clip
    }

    @FXML
    private fun onTogglePassword() {
        val showPassword = !passwordVisibleField.isVisible
        passwordVisibleField.isVisible = showPassword
        passwordVisibleField.isManaged = showPassword
        passwordField.isVisible = !showPassword
        passwordField.isManaged = !showPassword
    }

    @FXML
    private fun onLogin() {
        val isValidLogin = userField.text == ADMIN_USER && passwordField.text == ADMIN_PASSWORD
        if (isValidLogin) {
            openHomeView()
            return
        }

        Alert(Alert.AlertType.ERROR).apply {
            title = "Inicio de sesion"
            headerText = null
            contentText = "Credenciales incorrectas"
            showAndWait()
        }
    }

    private fun openHomeView() {
        Navigator.goToHome()
    }

    private companion object {
        const val ADMIN_USER = "admin"
        const val ADMIN_PASSWORD = "123"
    }
}

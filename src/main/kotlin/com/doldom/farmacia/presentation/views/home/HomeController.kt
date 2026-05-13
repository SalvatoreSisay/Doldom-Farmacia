package com.doldom.farmacia.presentation.views.home

import com.doldom.farmacia.core.utils.UiState
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.layout.AnchorPane

class HomeController {
    @FXML
    private lateinit var root: AnchorPane

    private val viewModel = HomeViewModel()

    @FXML
    private fun initialize() {
        viewModel.uiState.addListener { _, _, state ->
            when (state) {
                UiState.Loading -> root.isDisable = true
                is UiState.Success -> root.isDisable = false
                is UiState.Error -> {
                    root.isDisable = false
                    Alert(Alert.AlertType.ERROR).apply {
                        title = "Panel principal"
                        headerText = null
                        contentText = state.message
                        showAndWait()
                    }
                }
            }
        }

        viewModel.load()
    }
}

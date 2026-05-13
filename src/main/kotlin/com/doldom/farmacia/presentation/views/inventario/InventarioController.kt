package com.doldom.farmacia.presentation.views.inventario

import com.doldom.farmacia.core.utils.UiState
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane

class InventarioController {
    @FXML
    private lateinit var root: AnchorPane

    @FXML
    private lateinit var totalProductosValue: Label

    @FXML
    private lateinit var stockBajoValue: Label

    @FXML
    private lateinit var proximosVencerValue: Label

    private val viewModel = InventarioViewModel()

    @FXML
    private fun initialize() {
        viewModel.uiState.addListener { _, _, state ->
            when (state) {
                UiState.Loading -> root.isDisable = true
                is UiState.Success -> {
                    root.isDisable = false
                    totalProductosValue.text = state.data.totalProductos.toString()
                    stockBajoValue.text = state.data.stockBajo.toString()
                    proximosVencerValue.text = state.data.proximosVencer.toString()
                }
                is UiState.Error -> {
                    root.isDisable = false
                    Alert(Alert.AlertType.ERROR).apply {
                        title = "Inventario"
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

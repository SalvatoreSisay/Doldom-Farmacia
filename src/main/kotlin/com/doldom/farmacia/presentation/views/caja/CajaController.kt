package com.doldom.farmacia.presentation.views.caja

import com.doldom.farmacia.core.utils.UiState
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane

class CajaController {
    @FXML
    private lateinit var root: AnchorPane

    @FXML
    private lateinit var cashTotalValue: Label

    @FXML
    private lateinit var cardSalesValue: Label

    @FXML
    private lateinit var digitalTotalValue: Label

    @FXML
    private lateinit var expectedCashValue: Label

    @FXML
    private lateinit var registerIdValue: Label

    private val viewModel = CajaViewModel()

    @FXML
    private fun initialize() {
        viewModel.uiState.addListener { _, _, state ->
            when (state) {
                UiState.Loading -> root.isDisable = true
                is UiState.Success -> {
                    root.isDisable = false
                    cashTotalValue.text = state.data.cashTotal
                    cardSalesValue.text = state.data.cardSales
                    digitalTotalValue.text = state.data.digitalTotal
                    expectedCashValue.text = state.data.expectedCash
                    registerIdValue.text = "ID: ${state.data.registerId}"
                }
                is UiState.Error -> {
                    root.isDisable = false
                    Alert(Alert.AlertType.ERROR).apply {
                        title = "Caja diaria"
                        headerText = null
                        contentText = state.message
                        showAndWait()
                    }
                }
            }
        }

        viewModel.load()
    }

    @FXML
    private fun onInitializeSession() {
        Alert(Alert.AlertType.INFORMATION).apply {
            title = "Caja diaria"
            headerText = null
            contentText = "Sesion de caja inicializada."
            showAndWait()
        }
    }

    @FXML
    private fun onFinalizeRegister() {
        Alert(Alert.AlertType.INFORMATION).apply {
            title = "Caja diaria"
            headerText = null
            contentText = "Caja finalizada correctamente."
            showAndWait()
        }
    }
}

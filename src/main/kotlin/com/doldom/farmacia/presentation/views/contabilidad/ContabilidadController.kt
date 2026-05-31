package com.doldom.farmacia.presentation.views.contabilidad

import com.doldom.farmacia.core.utils.UiState
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane

class ContabilidadController {
    @FXML
    private lateinit var root: AnchorPane

    @FXML
    private lateinit var totalRevenueValue: Label

    @FXML
    private lateinit var operatingExpensesValue: Label

    @FXML
    private lateinit var netProfitValue: Label

    @FXML
    private lateinit var entriesChip: Label

    private val viewModel = ContabilidadViewModel()

    @FXML
    private fun initialize() {
        viewModel.uiState.addListener { _, _, state ->
            when (state) {
                UiState.Loading -> root.isDisable = true
                is UiState.Success -> {
                    root.isDisable = false
                    totalRevenueValue.text = state.data.totalRevenue
                    operatingExpensesValue.text = state.data.operatingExpenses
                    netProfitValue.text = state.data.netProfit
                    entriesChip.text = "Mostrando ${state.data.ledgerEntries} entradas"
                }
                is UiState.Error -> {
                    root.isDisable = false
                    Alert(Alert.AlertType.ERROR).apply {
                        title = "Contabilidad"
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

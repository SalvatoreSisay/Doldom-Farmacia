package com.doldom.farmacia.presentation.views.caja

import com.doldom.farmacia.core.utils.BaseViewModel
import com.doldom.farmacia.core.utils.UiState

class CajaViewModel : BaseViewModel<CajaUiData>() {
    fun load() {
        _uiState.set(
            UiState.Success(
                CajaUiData(
                    cashTotal = "Q4,280.50",
                    cardSales = "Q8,144.00",
                    digitalTotal = "Q1,920.25",
                    expectedCash = "Q4,280.50",
                    registerId = "REG-00452-D"
                )
            )
        )
    }
}

data class CajaUiData(
    val cashTotal: String,
    val cardSales: String,
    val digitalTotal: String,
    val expectedCash: String,
    val registerId: String
)

package com.doldom.farmacia.presentation.views.contabilidad

import com.doldom.farmacia.core.utils.BaseViewModel
import com.doldom.farmacia.core.utils.UiState

class ContabilidadViewModel : BaseViewModel<ContabilidadUiData>() {
    fun load() {
        _uiState.set(
            UiState.Success(
                ContabilidadUiData(
                    totalRevenue = "Q142,580.00",
                    operatingExpenses = "Q58,240.50",
                    netProfit = "Q84,339.50",
                    ledgerEntries = 142
                )
            )
        )
    }
}

data class ContabilidadUiData(
    val totalRevenue: String,
    val operatingExpenses: String,
    val netProfit: String,
    val ledgerEntries: Int
)

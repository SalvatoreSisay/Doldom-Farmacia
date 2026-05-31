package com.doldom.farmacia.presentation.views.ventas

import com.doldom.farmacia.core.utils.BaseViewModel
import com.doldom.farmacia.core.utils.UiState

class VentasViewModel : BaseViewModel<VentasUiData>() {
    fun load() {
        _uiState.set(
            UiState.Success(
                VentasUiData(
                    subtotal = "Q49.00",
                    tax = "Q2.45",
                    total = "Q51.45",
                    queueCount = 3
                )
            )
        )
    }
}

data class VentasUiData(
    val subtotal: String,
    val tax: String,
    val total: String,
    val queueCount: Int
)

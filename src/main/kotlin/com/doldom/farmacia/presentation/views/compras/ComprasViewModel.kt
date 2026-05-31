package com.doldom.farmacia.presentation.views.compras

import com.doldom.farmacia.core.utils.BaseViewModel
import com.doldom.farmacia.core.utils.UiState

class ComprasViewModel : BaseViewModel<ComprasUiData>() {
    fun load() {
        _uiState.set(
            UiState.Success(
                ComprasUiData(
                    subtotal = "Q1,500.00",
                    shippingFees = "Q45.00",
                    tax = "Q77.25",
                    total = "Q1,622.25",
                    paymentStatus = "Vence en 30 dias"
                )
            )
        )
    }
}

data class ComprasUiData(
    val subtotal: String,
    val shippingFees: String,
    val tax: String,
    val total: String,
    val paymentStatus: String
)

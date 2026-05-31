package com.doldom.farmacia.presentation.views.inventario

import com.doldom.farmacia.core.utils.BaseViewModel
import com.doldom.farmacia.core.utils.UiState
import com.doldom.farmacia.presentation.state.AppSession

class InventarioViewModel : BaseViewModel<InventarioUiData>() {
    fun load() {
        _uiState.set(
            UiState.Success(
                InventarioUiData(
                    totalProductos = AppSession.inventoryItems().size,
                    stockBajo = AppSession.lowStockProductsCount(),
                    proximosVencer = AppSession.expiringSoonProductsCount(),
                    saludInventario = AppSession.inventoryFreshnessPercent()
                )
            )
        )
    }
}

data class InventarioUiData(
    val totalProductos: Int,
    val stockBajo: Int,
    val proximosVencer: Int,
    val saludInventario: Int
)

package com.doldom.farmacia.presentation.views.inventario

import com.doldom.farmacia.core.utils.BaseViewModel
import com.doldom.farmacia.core.utils.UiState

class InventarioViewModel : BaseViewModel<InventarioUiData>() {
    fun load() {
        _uiState.set(
            UiState.Success(
                InventarioUiData(
                    totalProductos = 1240,
                    stockBajo = 18,
                    proximosVencer = 12
                )
            )
        )
    }
}

data class InventarioUiData(
    val totalProductos: Int,
    val stockBajo: Int,
    val proximosVencer: Int
)

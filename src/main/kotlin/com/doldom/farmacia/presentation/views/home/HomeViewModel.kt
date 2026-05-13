package com.doldom.farmacia.presentation.views.home

import com.doldom.farmacia.core.utils.BaseViewModel
import com.doldom.farmacia.core.utils.UiState

class HomeViewModel : BaseViewModel<HomeUiData>() {
    fun load() {
        _uiState.set(
            UiState.Success(
                HomeUiData(
                    pharmacistName = "Dra. Thorne",
                    shiftStatus = "ACTIVO",
                    shiftTime = "05h 24m",
                    totalSales = "Q2,481.51"
                )
            )
        )
    }
}

data class HomeUiData(
    val pharmacistName: String,
    val shiftStatus: String,
    val shiftTime: String,
    val totalSales: String
)

package com.doldom.farmacia.presentation.components.home

import com.doldom.farmacia.presentation.state.AppSession
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar

class StoreHealthController {
    @FXML
    private lateinit var freshnessValue: Label

    @FXML
    private lateinit var freshnessProgress: ProgressBar

    @FXML
    private fun initialize() {
        val percent = AppSession.inventoryFreshnessPercent()
        freshnessValue.text = "$percent%"
        freshnessProgress.progress = percent / 100.0
    }
}

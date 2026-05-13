package com.doldom.farmacia.presentation.components.inventory

import com.doldom.farmacia.presentation.navigation.Navigator
import javafx.fxml.FXML

class SidebarInventoryController {
    @FXML
    private fun onOpenHome() {
        Navigator.goToHome()
    }
}

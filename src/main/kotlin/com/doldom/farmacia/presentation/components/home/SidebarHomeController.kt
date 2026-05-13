package com.doldom.farmacia.presentation.components.home

import com.doldom.farmacia.presentation.navigation.Navigator
import javafx.fxml.FXML

class SidebarHomeController {
    @FXML
    private fun onOpenInventario() {
        Navigator.goToInventario()
    }
}

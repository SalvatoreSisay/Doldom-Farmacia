package com.doldom.farmacia.presentation.components.calendar

import com.doldom.farmacia.presentation.components.prescription.PrescriptionDialog
import com.doldom.farmacia.presentation.navigation.Navigator
import javafx.fxml.FXML

class SidebarCalendarController {
    @FXML
    private fun onOpenHome() {
        Navigator.goToHome()
    }

    @FXML
    private fun onOpenInventario() {
        Navigator.goToInventario()
    }

    @FXML
    private fun onOpenVentas() {
        Navigator.goToVentas()
    }

    @FXML
    private fun onOpenCompras() {
        Navigator.goToCompras()
    }

    @FXML
    private fun onOpenCaja() {
        Navigator.goToCaja()
    }

    @FXML
    private fun onOpenContabilidad() {
        Navigator.goToContabilidad()
    }

    @FXML
    private fun onNewPrescription() {
        PrescriptionDialog.show()
    }
}

package com.doldom.farmacia.presentation.components.home

import com.doldom.farmacia.presentation.navigation.Navigator
import javafx.animation.Interpolator
import javafx.animation.ScaleTransition
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.util.Duration

class QuickActionsController {
    @FXML
    private fun onOpenVentas(event: MouseEvent) {
        animateThenNavigate(event.source as Node) { Navigator.goToVentas() }
    }

    @FXML
    private fun onOpenCompras(event: MouseEvent) {
        animateThenNavigate(event.source as Node) { Navigator.goToCompras() }
    }

    @FXML
    private fun onOpenInventario(event: MouseEvent) {
        animateThenNavigate(event.source as Node) { Navigator.goToInventario() }
    }

    private fun animateThenNavigate(node: Node, action: () -> Unit) {
        ScaleTransition(Duration.millis(130.0), node).apply {
            fromX = 1.0
            fromY = 1.0
            toX = 0.97
            toY = 0.97
            cycleCount = 2
            isAutoReverse = true
            interpolator = Interpolator.EASE_BOTH
            setOnFinished { action() }
            play()
        }
    }
}

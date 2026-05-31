package com.doldom.farmacia.presentation.components.home

import com.doldom.farmacia.presentation.state.AppSession
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.util.Duration
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.Duration as JavaDuration
import java.util.Locale

class ShiftCardController {
    @FXML
    private lateinit var statusValue: Label

    @FXML
    private lateinit var elapsedValue: Label

    @FXML
    private lateinit var totalSalesValue: Label

    @FXML
    private lateinit var closeShiftButton: Button

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    private val ticker = Timeline()

    @FXML
    private fun initialize() {
        if (!AppSession.shiftActiveProperty.get() && AppSession.shiftClosedAtProperty.get() == null) {
            AppSession.startShift()
        }

        currencyFormat.currency = java.util.Currency.getInstance("GTQ")
        ticker.keyFrames.setAll(
            KeyFrame(Duration.ZERO, EventHandler { refreshElapsed() }),
            KeyFrame(Duration.seconds(1.0))
        )
        ticker.cycleCount = Timeline.INDEFINITE
        ticker.play()

        AppSession.totalSalesProperty.addListener { _, _, amount -> updateSales(amount) }
        AppSession.shiftActiveProperty.addListener { _, _, active -> updateShiftStatus(active) }

        updateSales(AppSession.totalSalesProperty.get())
        updateShiftStatus(AppSession.shiftActiveProperty.get())
        refreshElapsed()
    }

    @FXML
    private fun onCloseShift() {
        val result = Alert(Alert.AlertType.CONFIRMATION).apply {
            title = "Turno actual"
            headerText = "Cerrar turno"
            contentText = "Se cerrara el turno actual y se detendra el contador."
        }.showAndWait()

        if (result.orElse(ButtonType.CANCEL) != ButtonType.OK) return

        AppSession.closeShift()
        refreshElapsed()

        Alert(Alert.AlertType.INFORMATION).apply {
            title = "Turno actual"
            headerText = null
            contentText = "Turno cerrado. Total vendido: ${formatCurrency(AppSession.totalSalesProperty.get())}"
            showAndWait()
        }
    }

    private fun updateShiftStatus(active: Boolean) {
        statusValue.text = if (active) "ACTIVO" else "CERRADO"
        closeShiftButton.isDisable = !active
        if (active && ticker.status != Animation.Status.RUNNING) {
            ticker.play()
        } else if (!active) {
            ticker.stop()
        }
    }

    private fun updateSales(amount: BigDecimal) {
        totalSalesValue.text = formatCurrency(amount)
    }

    private fun refreshElapsed() {
        elapsedValue.text = formatElapsed(AppSession.currentShiftElapsed())
    }

    private fun formatElapsed(duration: JavaDuration): String {
        val hours = duration.toHours()
        val minutes = duration.toMinutesPart()
        val seconds = duration.toSecondsPart()
        return if (hours > 0) {
            "%02dh %02dm".format(hours, minutes)
        } else {
            "%02dm %02ds".format(minutes, seconds)
        }
    }

    private fun formatCurrency(amount: BigDecimal): String {
        return currencyFormat.format(amount).replace("GTQ", "Q").replace("GTQ", "Q")
    }
}

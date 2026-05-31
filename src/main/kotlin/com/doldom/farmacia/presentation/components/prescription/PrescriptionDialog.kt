package com.doldom.farmacia.presentation.components.prescription

import com.doldom.farmacia.presentation.state.AppSession
import javafx.beans.value.ChangeListener
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

object PrescriptionDialog {
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

    fun show() {
        currencyFormat.currency = java.util.Currency.getInstance("GTQ")

        val productMap = AppSession.inventoryItems().associateBy { it.name }
        val patientField = TextField().apply { promptText = "Nombre del paciente" }
        val productBox = ComboBox<String>().apply {
            items.setAll(productMap.keys)
            promptText = "Buscar medicamento..."
            isEditable = true
        }
        val doseField = TextField().apply { promptText = "Ej. 1 tableta cada 12 horas" }
        val quantityField = TextField("1")
        val datePicker = DatePicker(LocalDate.now())
        val priceField = TextField().apply { promptText = "Q0.00" }
        val totalLabel = Label("Q0.00").apply { styleClass.add("prescription-dialog-total") }
        val notesField = TextArea().apply {
            promptText = "Indicaciones, advertencias o duracion del tratamiento"
            prefRowCount = 4
            isWrapText = true
        }

        val refreshTotal = {
            val price = parseMoney(priceField.text)
            val quantity = quantityField.text.toIntOrNull()?.coerceAtLeast(1) ?: 1
            totalLabel.text = formatCurrency(price.multiply(BigDecimal(quantity)))
        }

        productBox.valueProperty().addListener { _, _, name ->
            productMap[name]?.let {
                priceField.text = formatCurrency(it.price)
                refreshTotal()
            }
        }
        val totalListener = ChangeListener<String> { _, _, _ -> refreshTotal() }
        quantityField.textProperty().addListener(totalListener)
        priceField.textProperty().addListener(totalListener)

        val content = GridPane().apply {
            hgap = 14.0
            vgap = 12.0
            padding = Insets(8.0, 4.0, 4.0, 4.0)
            add(fieldLabel("Paciente"), 0, 0)
            add(patientField, 1, 0)
            add(fieldLabel("Medicamento"), 0, 1)
            add(productBox, 1, 1)
            add(fieldLabel("Dosis"), 0, 2)
            add(doseField, 1, 2)
            add(fieldLabel("Fecha"), 0, 3)
            add(datePicker, 1, 3)
            add(fieldLabel("Cantidad"), 0, 4)
            add(quantityField, 1, 4)
            add(fieldLabel("Precio"), 0, 5)
            add(priceField, 1, 5)
            add(fieldLabel("Total"), 0, 6)
            add(totalLabel, 1, 6)
            add(fieldLabel("Indicaciones"), 0, 7)
            add(notesField, 1, 7)
            columnConstraints.add(javafx.scene.layout.ColumnConstraints().apply { minWidth = 108.0 })
            columnConstraints.add(javafx.scene.layout.ColumnConstraints().apply {
                hgrow = Priority.ALWAYS
                minWidth = 420.0
            })
        }

        val createButton = ButtonType("Guardar receta", ButtonBar.ButtonData.OK_DONE)
        val result = Dialog<Boolean>().apply {
            title = "Nueva receta"
            headerText = "Informacion de receta"
            dialogPane.content = content
            dialogPane.buttonTypes.addAll(createButton, ButtonType.CANCEL)
            PrescriptionDialog::class.java
                .getResource("/com/doldom/farmacia/presentation/views/home/home.css")
                ?.toExternalForm()
                ?.let { dialogPane.stylesheets.add(it) }
            setResultConverter { type ->
                type == createButton &&
                    patientField.text.isNotBlank() &&
                    productBox.editor.text.isNotBlank() &&
                    doseField.text.isNotBlank()
            }
        }.showAndWait()

        if (result.orElse(false)) {
            Alert(Alert.AlertType.INFORMATION).apply {
                title = "Nueva receta"
                headerText = null
                contentText = "Receta guardada para ${patientField.text.trim()}."
                showAndWait()
            }
        }
    }

    private fun fieldLabel(text: String): Label {
        return Label(text).apply { styleClass.add("prescription-dialog-label") }
    }

    private fun parseMoney(value: String): BigDecimal {
        val normalized = value.replace("Q", "").replace(",", "").trim()
        return normalized.toBigDecimalOrNull() ?: BigDecimal.ZERO
    }

    private fun formatCurrency(amount: BigDecimal): String {
        return currencyFormat
            .format(amount.setScale(2, RoundingMode.HALF_UP))
            .replace("GTQ", "Q")
    }
}

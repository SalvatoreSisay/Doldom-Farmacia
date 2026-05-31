package com.doldom.farmacia.presentation.views.compras

import com.doldom.farmacia.core.utils.UiState
import com.doldom.farmacia.presentation.state.AppSession
import com.doldom.farmacia.presentation.state.InventoryBatch
import javafx.animation.FadeTransition
import javafx.animation.Interpolator
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.util.Duration
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class ComprasController {
    @FXML
    private lateinit var root: AnchorPane

    @FXML
    private lateinit var searchShell: HBox

    @FXML
    private lateinit var searchField: TextField

    @FXML
    private lateinit var supplierCombo: ComboBox<String>

    @FXML
    private lateinit var invoiceField: TextField

    @FXML
    private lateinit var receivedRows: VBox

    @FXML
    private lateinit var subtotalValue: Label

    @FXML
    private lateinit var shippingField: TextField

    @FXML
    private lateinit var taxLabel: Label

    @FXML
    private lateinit var taxRateField: TextField

    @FXML
    private lateinit var taxValue: Label

    @FXML
    private lateinit var totalValue: Label

    @FXML
    private lateinit var paymentStatusValue: Label

    private val viewModel = ComprasViewModel()
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    private val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    private val purchaseRows = mutableListOf<PurchaseRow>()
    private var selectedRowId: String? = null

    @FXML
    private fun initialize() {
        currencyFormat.currency = java.util.Currency.getInstance("GTQ")

        supplierCombo.items.setAll(
            "AstraZeneca Pharmaceuticals",
            "PharmaLink Ltd",
            "Drogueria Central",
            "Sandoz Guatemala",
            "Distribuidora Salud Total"
        )
        supplierCombo.value = "AstraZeneca Pharmaceuticals"
        invoiceField.text = "INV-2026-001"

        searchField.textProperty().addListener { _, _, _ -> renderRows() }
        searchField.focusedProperty().addListener { _, _, focused -> animateSearch(focused) }
        shippingField.textProperty().addListener { _, _, _ -> recalculateSummary() }
        taxRateField.textProperty().addListener { _, _, _ -> recalculateSummary() }

        viewModel.uiState.addListener { _, _, state ->
            when (state) {
                UiState.Loading -> root.isDisable = true
                is UiState.Success -> {
                    root.isDisable = false
                    loadInitialRows()
                    paymentStatusValue.text = state.data.paymentStatus
                    renderRows()
                    recalculateSummary()
                }
                is UiState.Error -> {
                    root.isDisable = false
                    Alert(Alert.AlertType.ERROR).apply {
                        title = "Compras"
                        headerText = null
                        contentText = state.message
                        showAndWait()
                    }
                }
            }
        }

        viewModel.load()
    }

    @FXML
    private fun onAddRow() {
        val product = AppSession.inventoryItems()
            .firstOrNull { existing -> purchaseRows.none { it.product.sku == existing.sku } }
            ?: AppSession.inventoryItems().first()
        purchaseRows.add(PurchaseRow(product = product, quantity = 1, unitCost = product.price, expirationDate = product.expirationDate))
        selectedRowId = purchaseRows.last().id
        renderRows()
        recalculateSummary()
    }

    @FXML
    private fun onOpenAddProductDialog() {
        createProductDialog().showAndWait().ifPresent { row ->
            purchaseRows.add(row)
            selectedRowId = row.id
            renderRows()
            recalculateSummary()
        }
    }

    @FXML
    private fun onEditPaymentStatus() {
        val statusBox = ComboBox<String>().apply {
            items.setAll("Pendiente", "Vence en 15 dias", "Vence en 30 dias", "Pagado", "Retenido para revision")
            value = paymentStatusValue.text
            maxWidth = Double.MAX_VALUE
        }

        Dialog<String>().apply {
            title = "Estado de pago"
            headerText = "Actualizar estado de pago"
            dialogPane.content = VBox(12.0, Label("Estado"), statusBox).apply {
                padding = Insets(8.0)
            }
            dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
            setResultConverter { if (it == ButtonType.OK) statusBox.value else null }
        }.showAndWait().ifPresent {
            paymentStatusValue.text = it
        }
    }

    @FXML
    private fun onRegisterPurchase() {
        if (purchaseRows.isEmpty()) {
            showWarning("Agrega al menos un producto recibido.")
            return
        }
        if (supplierCombo.value.isNullOrBlank()) {
            showWarning("Selecciona un proveedor.")
            return
        }
        if (invoiceField.text.isBlank()) {
            showWarning("Ingresa el numero de factura.")
            return
        }

        val total = totalValue.text
        Alert(Alert.AlertType.INFORMATION).apply {
            title = "Compras"
            headerText = "Compra registrada"
            contentText = "Factura ${invoiceField.text.trim()} registrada por $total."
            showAndWait()
        }

        purchaseRows.clear()
        selectedRowId = null
        invoiceField.clear()
        paymentStatusValue.text = "Pendiente"
        renderRows()
        recalculateSummary()
    }

    @FXML
    private fun onSaveDraft() {
        Alert(Alert.AlertType.INFORMATION).apply {
            title = "Compras"
            headerText = "Borrador guardado"
            contentText = "Se guardo un borrador con ${purchaseRows.size} productos para ${supplierCombo.value ?: "proveedor pendiente"}."
            showAndWait()
        }
    }

    private fun loadInitialRows() {
        if (purchaseRows.isNotEmpty()) return
        val items = AppSession.inventoryItems()
        purchaseRows.add(PurchaseRow(items[0], 50, items[0].price, items[0].expirationDate))
        purchaseRows.add(PurchaseRow(items[1], 100, items[1].price, items[1].expirationDate))
    }

    private fun renderRows() {
        val query = searchField.text.trim().lowercase()
        val rows = purchaseRows
            .filter { query.isBlank() || it.product.name.lowercase().contains(query) }
            .map { createReceivedRow(it) }

        if (rows.isEmpty()) {
            receivedRows.children.setAll(
                Label("No hay productos recibidos para mostrar.").apply {
                    styleClass.add("received-empty-text")
                }
            )
        } else {
            receivedRows.children.setAll(rows)
        }
    }

    private fun createReceivedRow(row: PurchaseRow): HBox {
        val selected = selectedRowId == row.id
        return HBox(22.0).apply {
            alignment = Pos.CENTER_LEFT
            styleClass.add("received-row")
            if (selected) styleClass.add("selected")
            setOnMouseClicked {
                selectedRowId = row.id
                renderRows()
            }
            children.addAll(
                VBox(12.0).apply {
                    styleClass.add("received-name-col")
                    children.addAll(
                        Label("PRODUCTO").apply { styleClass.add("received-header") },
                        Label(row.product.name).apply { styleClass.add("received-product-name") }
                    )
                },
                editableCell("CANTIDAD", row.quantity.toString(), "received-qty-col") { value ->
                    row.quantity = value.toIntOrNull()?.coerceAtLeast(1) ?: row.quantity
                    recalculateSummary()
                    renderRows()
                },
                editableCell("COSTO", formatPlainMoney(row.unitCost), "received-cost-col") { value ->
                    row.unitCost = parseMoney(value).takeIf { it > BigDecimal.ZERO } ?: row.unitCost
                    recalculateSummary()
                    renderRows()
                },
                VBox(12.0).apply {
                    styleClass.add("received-date-col")
                    children.addAll(
                        Label("VENCIMIENTO").apply { styleClass.add("received-header") },
                        Label(row.expirationDate.format(dateFormatter)).apply { styleClass.add("received-value") }
                    )
                },
                Label("\uE787").apply {
                    styleClass.addAll("icon-mdl", "received-calendar")
                    setOnMouseClicked {
                        it.consume()
                        editExpirationDate(row)
                    }
                },
                Label("\uE74D").apply {
                    styleClass.addAll("icon-mdl", "received-delete")
                    setOnMouseClicked {
                        it.consume()
                        purchaseRows.removeIf { current -> current.id == row.id }
                        if (selectedRowId == row.id) selectedRowId = null
                        renderRows()
                        recalculateSummary()
                    }
                }
            )
            fadeIn(this)
        }
    }

    private fun editableCell(header: String, value: String, style: String, onCommit: (String) -> Unit): VBox {
        val field = TextField(value).apply {
            styleClass.add("received-edit-field")
            focusedProperty().addListener { _, wasFocused, focused ->
                if (wasFocused && !focused) onCommit(text)
            }
            setOnAction { onCommit(text) }
        }

        return VBox(8.0).apply {
            styleClass.add(style)
            children.addAll(
                Label(header).apply { styleClass.add("received-header") },
                field
            )
        }
    }

    private fun editExpirationDate(row: PurchaseRow) {
        val picker = DatePicker(row.expirationDate)
        Dialog<LocalDate>().apply {
            title = "Vencimiento"
            headerText = row.product.name
            dialogPane.content = VBox(12.0, Label("Fecha de vencimiento"), picker).apply {
                padding = Insets(8.0)
            }
            dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
            setResultConverter { if (it == ButtonType.OK) picker.value else null }
        }.showAndWait().ifPresent {
            row.expirationDate = it
            renderRows()
        }
    }

    private fun createProductDialog(): Dialog<PurchaseRow> {
        val productBox = ComboBox<String>().apply {
            items.setAll(AppSession.inventoryItems().map { it.name })
            value = items.firstOrNull()
            maxWidth = Double.MAX_VALUE
        }
        val quantityField = TextField("1")
        val costField = TextField(productBox.value?.let { selected ->
            AppSession.inventoryItems().first { it.name == selected }.price.toPlainString()
        } ?: "0.00")
        val expirationPicker = DatePicker(LocalDate.now().plusMonths(12))

        productBox.valueProperty().addListener { _, _, name ->
            AppSession.inventoryItems().firstOrNull { it.name == name }?.let {
                costField.text = it.price.toPlainString()
                expirationPicker.value = it.expirationDate
            }
        }

        val content = GridPane().apply {
            hgap = 14.0
            vgap = 12.0
            padding = Insets(8.0)
            add(Label("Producto"), 0, 0)
            add(productBox, 1, 0)
            add(Label("Cantidad"), 0, 1)
            add(quantityField, 1, 1)
            add(Label("Costo unitario"), 0, 2)
            add(costField, 1, 2)
            add(Label("Vencimiento"), 0, 3)
            add(expirationPicker, 1, 3)
            columnConstraints.add(javafx.scene.layout.ColumnConstraints().apply { minWidth = 110.0 })
            columnConstraints.add(javafx.scene.layout.ColumnConstraints().apply {
                hgrow = Priority.ALWAYS
                minWidth = 360.0
            })
        }

        val addButton = ButtonType("Agregar producto", ButtonBar.ButtonData.OK_DONE)
        return Dialog<PurchaseRow>().apply {
            title = "Productos recibidos"
            headerText = "Agregar producto a la compra"
            dialogPane.content = content
            dialogPane.buttonTypes.addAll(addButton, ButtonType.CANCEL)
            setResultConverter { type ->
                if (type != addButton) return@setResultConverter null
                val product = AppSession.inventoryItems().firstOrNull { it.name == productBox.value } ?: return@setResultConverter null
                PurchaseRow(
                    product = product,
                    quantity = quantityField.text.toIntOrNull()?.coerceAtLeast(1) ?: 1,
                    unitCost = parseMoney(costField.text).takeIf { it > BigDecimal.ZERO } ?: product.price,
                    expirationDate = expirationPicker.value ?: product.expirationDate
                )
            }
        }
    }

    private fun recalculateSummary() {
        val subtotal = purchaseRows.fold(BigDecimal.ZERO) { total, row ->
            total.add(row.unitCost.multiply(BigDecimal(row.quantity)))
        }
        val shipping = parseMoney(shippingField.text)
        val taxRate = taxRateField.text.toBigDecimalOrNull()?.coerceIn(BigDecimal.ZERO, BigDecimal("30")) ?: BigDecimal.ZERO
        val tax = subtotal.multiply(taxRate).divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
        val total = subtotal.add(shipping).add(tax)

        taxLabel.text = "Impuesto (${taxRate.stripTrailingZeros().toPlainString()}%)"
        subtotalValue.text = formatCurrency(subtotal)
        taxValue.text = formatCurrency(tax)
        totalValue.text = formatCurrency(total)
    }

    private fun animateSearch(focused: Boolean) {
        val width = if (focused) 760.0 else 560.0
        Timeline(
            javafx.animation.KeyFrame(
                Duration.millis(260.0),
                KeyValue(searchShell.prefWidthProperty(), width, Interpolator.EASE_BOTH)
            )
        ).play()
    }

    private fun fadeIn(node: Node) {
        FadeTransition(Duration.millis(160.0), node).apply {
            fromValue = 0.0
            toValue = 1.0
            interpolator = Interpolator.EASE_OUT
            play()
        }
    }

    private fun showWarning(message: String) {
        Alert(Alert.AlertType.WARNING).apply {
            title = "Compras"
            headerText = null
            contentText = message
            showAndWait()
        }
    }

    private fun parseMoney(value: String): BigDecimal {
        return value.replace("Q", "").replace(",", "").trim().toBigDecimalOrNull() ?: BigDecimal.ZERO
    }

    private fun formatCurrency(amount: BigDecimal): String {
        return currencyFormat.format(amount.setScale(2, RoundingMode.HALF_UP)).replace("GTQ", "Q")
    }

    private fun formatPlainMoney(amount: BigDecimal): String {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString()
    }
}

private data class PurchaseRow(
    val product: InventoryBatch,
    var quantity: Int,
    var unitCost: BigDecimal,
    var expirationDate: LocalDate,
    val id: String = UUID.randomUUID().toString()
)

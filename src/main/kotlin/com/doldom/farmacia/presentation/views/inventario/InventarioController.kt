package com.doldom.farmacia.presentation.views.inventario

import com.doldom.farmacia.core.utils.UiState
import com.doldom.farmacia.presentation.state.AppSession
import com.doldom.farmacia.presentation.state.InventoryBatch
import javafx.animation.FadeTransition
import javafx.animation.Interpolator
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.animation.TranslateTransition
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.util.Duration
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.ceil

class InventarioController {
    @FXML
    private lateinit var root: AnchorPane

    @FXML
    private lateinit var searchShell: HBox

    @FXML
    private lateinit var searchField: TextField

    @FXML
    private lateinit var inventoryHealthValue: Label

    @FXML
    private lateinit var inventoryHealthDelta: Label

    @FXML
    private lateinit var inventoryHealthProgress: ProgressBar

    @FXML
    private lateinit var totalProductosValue: Label

    @FXML
    private lateinit var stockBajoValue: Label

    @FXML
    private lateinit var proximosVencerValue: Label

    @FXML
    private lateinit var inventoryRows: VBox

    @FXML
    private lateinit var paginationText: Label

    @FXML
    private lateinit var pageOne: Label

    @FXML
    private lateinit var pageTwo: Label

    @FXML
    private lateinit var pageThree: Label

    @FXML
    private lateinit var inventoryFlowPanel: VBox

    @FXML
    private lateinit var inventoryFlowTab: HBox

    @FXML
    private lateinit var flowDailyValue: Label

    @FXML
    private lateinit var flowFrequencyValue: Label

    private val viewModel = InventarioViewModel()
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy")
    private var currentPage = 1
    private var selectedSku: String? = null
    private var activeFilter: InventoryFilter = InventoryFilter.ALL

    @FXML
    private fun initialize() {
        currencyFormat.currency = java.util.Currency.getInstance("GTQ")

        searchField.textProperty().addListener { _, _, _ ->
            currentPage = 1
            activeFilter = InventoryFilter.ALL
            renderTable()
        }
        searchField.focusedProperty().addListener { _, _, focused -> animateSearch(focused) }

        viewModel.uiState.addListener { _, _, state ->
            when (state) {
                UiState.Loading -> root.isDisable = true
                is UiState.Success -> {
                    root.isDisable = false
                    applyMetrics(state.data)
                    renderTable()
                    renderFlow()
                }
                is UiState.Error -> {
                    root.isDisable = false
                    Alert(Alert.AlertType.ERROR).apply {
                        title = "Inventario"
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
    private fun onShowInventoryHealth() {
        val health = AppSession.inventoryFreshnessPercent()
        val lowStock = AppSession.lowStockProductsCount()
        val expiring = AppSession.expiringSoonProductsCount()
        Alert(Alert.AlertType.INFORMATION).apply {
            title = "Salud de inventario"
            headerText = "Resumen operativo"
            contentText = "Salud actual: $health%\nProductos con stock bajo: $lowStock\nProductos proximos a vencer: $expiring"
            showAndWait()
        }
    }

    @FXML
    private fun onShowLowStock() {
        activeFilter = InventoryFilter.LOW_STOCK
        currentPage = 1
        renderTable()
    }

    @FXML
    private fun onShowExpiringSoon() {
        activeFilter = InventoryFilter.EXPIRING
        currentPage = 1
        renderTable()
    }

    @FXML
    private fun onGoPageOne() = goToPage(1)

    @FXML
    private fun onGoPageTwo() = goToPage(2)

    @FXML
    private fun onGoPageThree() = goToPage(3)

    @FXML
    private fun onToggleFlowPanel() {
        val closing = inventoryFlowPanel.isVisible
        val target = if (closing) inventoryFlowPanel else inventoryFlowTab
        val next = if (closing) inventoryFlowTab else inventoryFlowPanel

        FadeTransition(Duration.millis(140.0), target).apply {
            fromValue = 1.0
            toValue = 0.0
            interpolator = Interpolator.EASE_BOTH
            setOnFinished {
                target.isVisible = false
                target.isManaged = false
                next.opacity = 0.0
                next.isVisible = true
                next.isManaged = true
                FadeTransition(Duration.millis(180.0), next).apply {
                    fromValue = 0.0
                    toValue = 1.0
                    interpolator = Interpolator.EASE_OUT
                    play()
                }
            }
            play()
        }
    }

    private fun applyMetrics(data: InventarioUiData) {
        totalProductosValue.text = "${data.totalProductos} SKU TOTAL"
        stockBajoValue.text = data.stockBajo.toString().padStart(2, '0')
        proximosVencerValue.text = data.proximosVencer.toString().padStart(2, '0')
        inventoryHealthValue.text = "${data.saludInventario}%"
        inventoryHealthProgress.progress = data.saludInventario / 100.0
        inventoryHealthDelta.text = if (data.saludInventario >= 85) "estable" else "riesgo"
    }

    private fun renderFlow() {
        val totalUnits = AppSession.totalInventoryUnits()
        val lowStock = AppSession.lowStockProductsCount()
        flowDailyValue.text = (totalUnits / 7).toString()
        flowFrequencyValue.text = if (lowStock == 0) "7.0 dias" else "%.1f dias".format(7.0 / lowStock.coerceAtLeast(1))
    }

    private fun renderTable() {
        val products = filteredProducts()
        val totalPages = ceil(products.size / PAGE_SIZE.toDouble()).toInt().coerceAtLeast(1)
        currentPage = currentPage.coerceIn(1, totalPages)
        val fromIndex = ((currentPage - 1) * PAGE_SIZE).coerceAtMost(products.size)
        val toIndex = (fromIndex + PAGE_SIZE).coerceAtMost(products.size)
        val pageItems = products.subList(fromIndex, toIndex)

        inventoryRows.children.setAll(pageItems.map { createRow(it) })

        paginationText.text = if (products.isEmpty()) {
            "No hay productos para mostrar"
        } else {
            "Mostrando ${fromIndex + 1} a $toIndex de ${products.size} productos"
        }

        updatePageButton(pageOne, 1, totalPages)
        updatePageButton(pageTwo, 2, totalPages)
        updatePageButton(pageThree, 3, totalPages)
    }

    private fun filteredProducts(): List<InventoryBatch> {
        val query = searchField.text.trim().lowercase()
        val today = LocalDate.now()
        val expiringLimit = today.plusDays(EXPIRING_SOON_DAYS)

        return AppSession.inventoryItems()
            .asSequence()
            .filter {
                query.isBlank() ||
                    it.name.lowercase().contains(query) ||
                    it.sku.lowercase().contains(query)
            }
            .filter {
                when (activeFilter) {
                    InventoryFilter.ALL -> true
                    InventoryFilter.LOW_STOCK -> it.quantity <= it.minStock
                    InventoryFilter.EXPIRING -> !it.expirationDate.isAfter(expiringLimit)
                }
            }
            .sortedBy { it.name }
            .toList()
    }

    private fun createRow(product: InventoryBatch): HBox {
        val isLowStock = product.quantity <= product.minStock
        val row = HBox(0.0).apply {
            styleClass.add("inventory-row")
            if (isLowStock) styleClass.add("inventory-row-warn")
            if (selectedSku == product.sku) styleClass.add("selected")
            setOnMouseClicked {
                selectedSku = product.sku
                renderTable()
            }
        }

        val productCell = HBox(12.0).apply {
            alignment = Pos.CENTER_LEFT
            styleClass.addAll("inventory-col", "col-name")
            children.addAll(
                StackPane().apply {
                    styleClass.add("inventory-prod-icon-shell")
                    if (isLowStock) styleClass.add("warn")
                    children.add(Label(if (isLowStock) "\uE7BA" else "\uE8B0").apply {
                        styleClass.addAll("icon-mdl", "inventory-prod-icon")
                        if (isLowStock) styleClass.add("warn")
                    })
                },
                VBox().apply {
                    children.addAll(
                        Label(product.name).apply { styleClass.add("inventory-prod-name") },
                        Label("SKU: ${product.sku}").apply { styleClass.add("inventory-prod-sku") }
                    )
                }
            )
        }

        val categoryCell = HBox().apply {
            alignment = Pos.CENTER_LEFT
            styleClass.addAll("inventory-col", "col-cat")
            children.add(Label(product.category).apply {
                styleClass.add("inventory-cat-chip")
                if (isLowStock) styleClass.add("neutral")
            })
        }

        val stockProgress = (product.quantity.toDouble() / (product.minStock * 4).toDouble()).coerceIn(0.05, 1.0)
        val stockCell = HBox(10.0).apply {
            alignment = Pos.CENTER_LEFT
            styleClass.addAll("inventory-col", "col-stock")
            children.addAll(
                Label("${product.quantity} Unidades").apply {
                    styleClass.add("inventory-stock-value")
                    if (isLowStock) styleClass.add("warn")
                },
                ProgressBar(stockProgress).apply {
                    styleClass.add("inventory-stock-progress")
                    if (isLowStock) styleClass.add("warn")
                }
            )
        }

        row.children.addAll(
            productCell,
            categoryCell,
            stockCell,
            Label(formatCurrency(product.price)).apply { styleClass.addAll("inventory-col", "col-price", "inventory-price") },
            Label(product.expirationDate.format(dateFormatter)).apply { styleClass.addAll("inventory-col", "col-exp", "inventory-exp-date") }
        )

        FadeTransition(Duration.millis(160.0), row).apply {
            fromValue = 0.0
            toValue = 1.0
            play()
        }
        return row
    }

    private fun goToPage(page: Int) {
        val totalPages = ceil(filteredProducts().size / PAGE_SIZE.toDouble()).toInt().coerceAtLeast(1)
        if (page > totalPages) return
        currentPage = page
        renderTable()
    }

    private fun updatePageButton(button: Label, page: Int, totalPages: Int) {
        button.isVisible = page <= totalPages
        button.isManaged = page <= totalPages
        button.styleClass.remove("active")
        if (currentPage == page) button.styleClass.add("active")
    }

    private fun animateSearch(focused: Boolean) {
        val width = if (focused) 780.0 else 520.0
        Timeline(
            javafx.animation.KeyFrame(
                Duration.millis(260.0),
                KeyValue(searchShell.prefWidthProperty(), width, Interpolator.EASE_BOTH)
            )
        ).play()
        if (focused) {
            TranslateTransition(Duration.millis(220.0), searchShell).apply {
                toX = 4.0
                isAutoReverse = true
                cycleCount = 2
                interpolator = Interpolator.EASE_BOTH
                play()
            }
        }
    }

    private fun formatCurrency(amount: BigDecimal): String {
        return currencyFormat.format(amount).replace("GTQ", "Q")
    }

    private enum class InventoryFilter {
        ALL,
        LOW_STOCK,
        EXPIRING
    }

    private companion object {
        const val PAGE_SIZE = 3
        const val EXPIRING_SOON_DAYS = 90L
    }
}

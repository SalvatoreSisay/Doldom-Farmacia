package com.doldom.farmacia.presentation.views.ventas

import com.doldom.farmacia.core.utils.UiState
import com.doldom.farmacia.presentation.state.AppSession
import com.doldom.farmacia.presentation.state.InventoryBatch
import javafx.animation.FadeTransition
import javafx.animation.Interpolator
import javafx.animation.ScaleTransition
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.util.Duration
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.ceil

class VentasController {
    @FXML
    private lateinit var root: AnchorPane

    @FXML
    private lateinit var allCategoryCard: HBox

    @FXML
    private lateinit var antibioticsCategoryCard: HBox

    @FXML
    private lateinit var vitaminsCategoryCard: HBox

    @FXML
    private lateinit var productsContainer: VBox

    @FXML
    private lateinit var productDots: HBox

    @FXML
    private lateinit var cartItemsContainer: VBox

    @FXML
    private lateinit var subtotalValue: Label

    @FXML
    private lateinit var taxSummaryLabel: Label

    @FXML
    private lateinit var taxRateField: TextField

    @FXML
    private lateinit var taxValue: Label

    @FXML
    private lateinit var totalValue: Label

    @FXML
    private lateinit var queueTitle: Label

    @FXML
    private lateinit var patientQueueContainer: HBox

    @FXML
    private lateinit var cashPayment: VBox

    @FXML
    private lateinit var cardPayment: VBox

    @FXML
    private lateinit var digitalPayment: VBox

    private val viewModel = VentasViewModel()
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    private val expirationFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    private val cart = linkedMapOf<String, CartItem>()
    private val productInfoPages = mutableMapOf<String, Int>()
    private val patientQueue = mutableListOf(
        Patient("Eleanor Rigby", "ER", 5, "one"),
        Patient("Arthur Dent", "AD", 12, "two"),
        Patient("Sarah Palmer", "SP", 15, "three")
    )
    private var currentFilter = SalesFilter.ALL
    private var productPage = 0
    private var selectedPayment = PaymentMethod.CASH

    @FXML
    private fun initialize() {
        currencyFormat.currency = java.util.Currency.getInstance("GTQ")

        taxRateField.textProperty().addListener { _, _, _ -> recalculateCart() }

        viewModel.uiState.addListener { _, _, state ->
            when (state) {
                UiState.Loading -> root.isDisable = true
                is UiState.Success -> {
                    root.isDisable = false
                    renderProducts()
                    renderCart()
                    renderPatients()
                    selectPayment(PaymentMethod.CASH)
                }
                is UiState.Error -> {
                    root.isDisable = false
                    Alert(Alert.AlertType.ERROR).apply {
                        title = "Ventas"
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
    private fun onFilterAll() = applyFilter(SalesFilter.ALL, allCategoryCard)

    @FXML
    private fun onFilterAntibiotics() = applyFilter(SalesFilter.ANTIBIOTICS, antibioticsCategoryCard)

    @FXML
    private fun onFilterVitamins() = applyFilter(SalesFilter.VITAMINS, vitaminsCategoryCard)

    @FXML
    private fun onClearCart() {
        cart.clear()
        renderCart()
    }

    @FXML
    private fun onSelectCash() = selectPayment(PaymentMethod.CASH)

    @FXML
    private fun onSelectCard() = selectPayment(PaymentMethod.CARD)

    @FXML
    private fun onSelectDigital() = selectPayment(PaymentMethod.DIGITAL)

    @FXML
    private fun onNextPatient() {
        if (patientQueue.isEmpty()) {
            Alert(Alert.AlertType.INFORMATION).apply {
                title = "Cola de pacientes"
                headerText = null
                contentText = "No hay pacientes en espera."
                showAndWait()
            }
            return
        }

        val patient = patientQueue.removeAt(0)
        renderPatients()
        Alert(Alert.AlertType.INFORMATION).apply {
            title = "Cola de pacientes"
            headerText = null
            contentText = "Paciente en atencion: ${patient.name}"
            showAndWait()
        }
    }

    @FXML
    private fun onCompleteSale() {
        val total = cartTotal()
        if (cart.isEmpty() || total <= BigDecimal.ZERO) {
            Alert(Alert.AlertType.WARNING).apply {
                title = "Ventas"
                headerText = null
                contentText = "Agrega al menos un producto al carrito."
                showAndWait()
            }
            return
        }

        if (!AppSession.shiftActiveProperty.get()) {
            Alert(Alert.AlertType.WARNING).apply {
                title = "Ventas"
                headerText = null
                contentText = "No se puede completar la venta porque el turno esta cerrado."
                showAndWait()
            }
            return
        }

        AppSession.addSale(total)
        cart.clear()
        renderCart()
        renderProducts()

        Alert(Alert.AlertType.INFORMATION).apply {
            title = "Ventas"
            headerText = null
            contentText = "Venta exitosa."
            showAndWait()
        }
    }

    private fun applyFilter(filter: SalesFilter, source: Node) {
        currentFilter = filter
        productPage = 0
        setActiveCategory(source)
        ScaleTransition(Duration.millis(180.0), source).apply {
            fromX = 0.96
            fromY = 0.96
            toX = 1.04
            toY = 1.04
            cycleCount = 2
            isAutoReverse = true
            interpolator = Interpolator.EASE_BOTH
            setOnFinished { renderProducts() }
            play()
        }
    }

    private fun renderProducts() {
        val products = filteredProducts()
        val pageCount = ceil(products.size / PRODUCTS_PER_PAGE.toDouble()).toInt().coerceAtLeast(1)
        productPage = productPage.coerceIn(0, pageCount - 1)

        val pageProducts = products
            .drop(productPage * PRODUCTS_PER_PAGE)
            .take(PRODUCTS_PER_PAGE)

        val rows = pageProducts.chunked(3).map { rowProducts ->
            HBox(20.0).apply {
                children.setAll(rowProducts.map { createProductCard(it) })
            }
        }

        productsContainer.children.setAll(rows)
        renderDots(pageCount)
        fadeIn(productsContainer)
    }

    private fun createProductCard(product: InventoryBatch): VBox {
        val remaining = remainingStock(product)
        val lowStock = remaining <= product.minStock
        val card = VBox(18.0).apply {
            styleClass.add("product-card")
        }

        val tagText = if (lowStock) "URGENTE" else when {
            product.category.contains("Suplement", ignoreCase = true) -> "BIENESTAR"
            product.category.contains("Antibi", ignoreCase = true) -> "MANTENIMIENTO"
            else -> "DISPONIBLE"
        }

        val header = HBox().apply {
            alignment = Pos.CENTER_LEFT
            children.addAll(
                Label(tagText).apply {
                    styleClass.add("product-tag")
                    styleClass.add(if (lowStock) "orange" else "green")
                },
                Region().apply { HBox.setHgrow(this, Priority.ALWAYS) },
                Label(formatCurrency(product.price)).apply { styleClass.add("product-price") }
            )
        }

        val infoPage = productInfoPages[product.sku] ?: 0
        val name = VBox(4.0).apply {
            children.addAll(
                Label(formatProductName(product.name)).apply { styleClass.add("product-name") },
                Label(productInfoText(product, remaining, infoPage)).apply {
                    styleClass.add("product-meta")
                    isWrapText = true
                }
            )
        }

        val addButton = Button("+").apply {
            isMnemonicParsing = false
            styleClass.add("sales-add-btn")
            setOnAction {
                addToCart(product)
                animateAdd(this)
            }
        }

        val footer = HBox().apply {
            alignment = Pos.CENTER_LEFT
            children.addAll(
                infoDots(product, infoPage),
                Region().apply { HBox.setHgrow(this, Priority.ALWAYS) },
                addButton
            )
        }

        card.children.addAll(header, name, Region().apply { VBox.setVgrow(this, Priority.ALWAYS) }, footer)
        return card
    }

    private fun infoDots(product: InventoryBatch, activePage: Int): HBox {
        return HBox(8.0).apply {
            repeat(3) { index ->
                children.add(Region().apply {
                    styleClass.add("stock-dot")
                    styleClass.add(if (index == activePage) "active" else "muted")
                    setOnMouseClicked {
                        productInfoPages[product.sku] = index
                        renderProducts()
                    }
                })
            }
        }
    }

    private fun productInfoText(product: InventoryBatch, remaining: Int, infoPage: Int): String {
        return when (infoPage) {
            1 -> "SKU ${product.sku} - vence ${product.expirationDate.format(expirationFormatter)}"
            2 -> "Minimo ${product.minStock} - ${formatCurrency(product.price)} c/u"
            else -> if (remaining <= product.minStock) {
                "Stock bajo - quedan $remaining unidades"
            } else {
                "${product.category} - $remaining unidades en stock"
            }
        }
    }

    private fun renderDots(pageCount: Int) {
        productDots.children.setAll((0 until pageCount).map { index ->
            Region().apply {
                styleClass.add("product-page-dot")
                if (index == productPage) styleClass.add("active")
                setOnMouseClicked {
                    productPage = index
                    renderProducts()
                }
            }
        })
    }

    private fun addToCart(product: InventoryBatch) {
        val remaining = remainingStock(product)
        if (remaining <= 0) {
            Alert(Alert.AlertType.WARNING).apply {
                title = "Stock"
                headerText = null
                contentText = "No quedan unidades disponibles de ${product.name}."
                showAndWait()
            }
            return
        }

        val current = cart[product.sku]
        cart[product.sku] = if (current == null) {
            CartItem(product, 1)
        } else {
            current.copy(quantity = current.quantity + 1)
        }

        val nextRemaining = remainingStock(product)
        if (nextRemaining <= product.minStock) {
            Alert(Alert.AlertType.INFORMATION).apply {
                title = "Stock bajo"
                headerText = product.name
                contentText = "Quedan $nextRemaining unidades disponibles."
                showAndWait()
            }
        }

        renderCart()
        renderProducts()
    }

    private fun renderCart() {
        if (cart.isEmpty()) {
            cartItemsContainer.children.setAll(
                Label("Carrito listo para una nueva compra").apply {
                    styleClass.add("cart-empty-text")
                    isWrapText = true
                }
            )
        } else {
            cartItemsContainer.children.setAll(cart.values.map { createCartRow(it) })
        }
        recalculateCart()
        fadeIn(cartItemsContainer)
    }

    private fun createCartRow(item: CartItem): HBox {
        val lineTotal = item.product.price.multiply(BigDecimal(item.quantity))
        return HBox(14.0).apply {
            alignment = Pos.CENTER_LEFT
            children.addAll(
                StackPane().apply {
                    styleClass.add("cart-qty")
                    children.add(Label("${item.quantity}x").apply { styleClass.add("cart-qty-text") })
                },
                VBox().apply {
                    HBox.setHgrow(this, Priority.ALWAYS)
                    children.addAll(
                        Label(item.product.name).apply { styleClass.add("cart-item-name") },
                        Label("SKU: ${item.product.sku}").apply { styleClass.add("cart-item-meta") }
                    )
                },
                Label(formatCurrency(lineTotal)).apply { styleClass.add("cart-line-price") },
                Label("\uE74D").apply {
                    styleClass.addAll("icon-mdl", "cart-item-delete")
                    setOnMouseClicked {
                        cart.remove(item.product.sku)
                        renderCart()
                        renderProducts()
                    }
                }
            )
        }
    }

    private fun recalculateCart() {
        val subtotal = cartSubtotal()
        val taxRate = taxRateField.text.toBigDecimalOrNull()?.coerceIn(BigDecimal.ZERO, BigDecimal("30")) ?: BigDecimal.ZERO
        val tax = subtotal.multiply(taxRate).divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
        val total = subtotal.add(tax)

        taxSummaryLabel.text = "Impuesto (${taxRate.stripTrailingZeros().toPlainString()}%)"
        subtotalValue.text = formatCurrency(subtotal)
        taxValue.text = formatCurrency(tax)
        totalValue.text = formatCurrency(total)
    }

    private fun selectPayment(method: PaymentMethod) {
        selectedPayment = method
        mapOf(
            PaymentMethod.CASH to cashPayment,
            PaymentMethod.CARD to cardPayment,
            PaymentMethod.DIGITAL to digitalPayment
        ).forEach { (payment, node) ->
            val active = payment == selectedPayment
            node.styleClass.remove("active")
            if (active) node.styleClass.add("active")
            node.children.forEach { child ->
                child.styleClass.remove("active")
                if (active && (child.styleClass.contains("payment-icon") || child.styleClass.contains("payment-label"))) {
                    child.styleClass.add("active")
                }
            }
        }
    }

    private fun renderPatients() {
        queueTitle.text = "COLA DE PACIENTES (${patientQueue.size})"
        patientQueueContainer.children.setAll(patientQueue.mapIndexed { index, patient ->
            HBox(12.0).apply {
                alignment = Pos.CENTER_LEFT
                styleClass.add("patient-pill")
                setOnMouseClicked {
                    patientQueue.remove(patient)
                    patientQueue.add(0, patient)
                    renderPatients()
                }
                children.addAll(
                    StackPane().apply {
                        styleClass.addAll("patient-avatar", patient.avatarStyle)
                        children.add(Label(patient.initials).apply { styleClass.add("patient-avatar-text") })
                    },
                    VBox().apply {
                        children.addAll(
                            Label(patient.name).apply { styleClass.add("patient-name") },
                            Label("Espera ${patient.waitMinutes + index * 2}m").apply { styleClass.add("patient-wait") }
                        )
                    }
                )
            }
        })
    }

    private fun filteredProducts(): List<InventoryBatch> {
        return AppSession.inventoryItems().filter {
            when (currentFilter) {
                SalesFilter.ALL -> true
                SalesFilter.ANTIBIOTICS -> it.category.contains("Antibi", ignoreCase = true)
                SalesFilter.VITAMINS -> it.category.contains("Suplement", ignoreCase = true) ||
                    it.name.contains("Vitamina", ignoreCase = true)
            }
        }
    }

    private fun remainingStock(product: InventoryBatch): Int {
        return product.quantity - (cart[product.sku]?.quantity ?: 0)
    }

    private fun cartSubtotal(): BigDecimal {
        return cart.values.fold(BigDecimal.ZERO) { total, item ->
            total.add(item.product.price.multiply(BigDecimal(item.quantity)))
        }
    }

    private fun cartTotal(): BigDecimal {
        val subtotal = cartSubtotal()
        val taxRate = taxRateField.text.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val tax = subtotal.multiply(taxRate).divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
        return subtotal.add(tax)
    }

    private fun setActiveCategory(activeNode: Node) {
        listOf(allCategoryCard, antibioticsCategoryCard, vitaminsCategoryCard).forEach {
            it.styleClass.remove("active")
        }
        activeNode.styleClass.add("active")
    }

    private fun animateAdd(node: Node) {
        ScaleTransition(Duration.millis(120.0), node).apply {
            fromX = 1.0
            fromY = 1.0
            toX = 1.16
            toY = 1.16
            cycleCount = 2
            isAutoReverse = true
            interpolator = Interpolator.EASE_BOTH
            play()
        }
    }

    private fun fadeIn(node: Node) {
        FadeTransition(Duration.millis(180.0), node).apply {
            fromValue = 0.0
            toValue = 1.0
            interpolator = Interpolator.EASE_OUT
            play()
        }
    }

    private fun formatCurrency(amount: BigDecimal): String {
        return currencyFormat.format(amount.setScale(2, RoundingMode.HALF_UP)).replace("GTQ", "Q")
    }

    private fun formatProductName(name: String): String {
        val parts = name.split(" ", limit = 2)
        return if (parts.size == 2) "${parts[0]}\n${parts[1]}" else name
    }

    private data class CartItem(
        val product: InventoryBatch,
        val quantity: Int
    )

    private data class Patient(
        val name: String,
        val initials: String,
        val waitMinutes: Int,
        val avatarStyle: String
    )

    private enum class SalesFilter {
        ALL,
        ANTIBIOTICS,
        VITAMINS
    }

    private enum class PaymentMethod {
        CASH,
        CARD,
        DIGITAL
    }

    private companion object {
        const val PRODUCTS_PER_PAGE = 6
    }
}

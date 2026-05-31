package com.doldom.farmacia.presentation.state

import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

object AppSession {
    private const val EXPIRING_SOON_DAYS = 90L

    val shiftStartedAtProperty: ObjectProperty<LocalDateTime> = SimpleObjectProperty(LocalDateTime.now())
    val shiftClosedAtProperty: ObjectProperty<LocalDateTime?> = SimpleObjectProperty(null)
    val shiftActiveProperty: BooleanProperty = SimpleBooleanProperty(false)
    val totalSalesProperty: ObjectProperty<BigDecimal> = SimpleObjectProperty(BigDecimal.ZERO)
    val tasks: ObservableList<PharmacyTask> = FXCollections.observableArrayList()

    private val inventory = listOf(
        InventoryBatch("Amoxicilina 500mg", "AMX-2938", "Antibioticos", 310, 90, LocalDate.now().plusDays(150), BigDecimal("12.50")),
        InventoryBatch("Lisinopril 10mg", "LIS-0041", "Cardiovascular", 42, 80, LocalDate.now().plusDays(2), BigDecimal("8.75")),
        InventoryBatch("Atorvastatina 40mg", "ATR-8821", "Colesterol", 186, 70, LocalDate.now().plusDays(210), BigDecimal("45.00")),
        InventoryBatch("Vitamina D3 2000IU", "VIT-2210", "Suplementos", 260, 90, LocalDate.now().plusDays(320), BigDecimal("18.25")),
        InventoryBatch("Metformina 500mg", "MET-7712", "Diabetes", 55, 75, LocalDate.now().plusDays(72), BigDecimal("8.90")),
        InventoryBatch("Ibuprofeno 400mg", "IBU-4001", "Analgesicos", 287, 100, LocalDate.now().plusDays(180), BigDecimal("5.40")),
        InventoryBatch("Omeprazol 20mg", "OME-6810", "Gastrointestinal", 64, 85, LocalDate.now().plusDays(42), BigDecimal("6.25")),
        InventoryBatch("Losartan 50mg", "LOS-1129", "Cardiovascular", 205, 80, LocalDate.now().plusDays(260), BigDecimal("10.40"))
    )

    fun startShift() {
        shiftStartedAtProperty.set(LocalDateTime.now())
        shiftClosedAtProperty.set(null)
        shiftActiveProperty.set(true)
        totalSalesProperty.set(BigDecimal.ZERO)
    }

    fun closeShift() {
        if (!shiftActiveProperty.get()) return
        shiftClosedAtProperty.set(LocalDateTime.now())
        shiftActiveProperty.set(false)
    }

    fun addSale(amount: BigDecimal) {
        if (!shiftActiveProperty.get() || amount <= BigDecimal.ZERO) return
        totalSalesProperty.set(totalSalesProperty.get().add(amount))
    }

    fun currentShiftElapsed(): Duration {
        val end = shiftClosedAtProperty.get() ?: LocalDateTime.now()
        return Duration.between(shiftStartedAtProperty.get(), end).coerceAtLeast(Duration.ZERO)
    }

    fun totalInventoryUnits(): Int = inventory.sumOf { it.quantity }

    fun lowStockProductsCount(): Int = inventory.count { it.quantity <= it.minStock }

    fun expiringSoonUnits(today: LocalDate = LocalDate.now()): Int {
        val limit = today.plusDays(EXPIRING_SOON_DAYS)
        return inventory
            .filter { !it.expirationDate.isAfter(limit) }
            .sumOf { it.quantity }
    }

    fun expiringSoonProductsCount(today: LocalDate = LocalDate.now()): Int {
        val limit = today.plusDays(EXPIRING_SOON_DAYS)
        return inventory.count { !it.expirationDate.isAfter(limit) }
    }

    fun inventoryFreshnessPercent(today: LocalDate = LocalDate.now()): Int {
        val totalUnits = totalInventoryUnits()
        if (totalUnits == 0) return 100

        val freshUnits = totalUnits - expiringSoonUnits(today)
        return BigDecimal(freshUnits)
            .multiply(BigDecimal(100))
            .divide(BigDecimal(totalUnits), 0, RoundingMode.HALF_UP)
            .toInt()
            .coerceIn(0, 100)
    }

    fun inventoryItems(): List<InventoryBatch> = inventory

    fun addTask(title: String, detail: String, dueDate: LocalDate, priority: TaskPriority, category: String) {
        tasks.add(
            PharmacyTask(
                id = UUID.randomUUID().toString(),
                title = title,
                detail = detail,
                dueDate = dueDate,
                priority = priority,
                category = category
            )
        )
    }

    fun toggleTask(task: PharmacyTask, completed: Boolean) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index == -1) return
        tasks[index] = task.copy(completed = completed)
    }
}

data class InventoryBatch(
    val name: String,
    val sku: String,
    val category: String,
    val quantity: Int,
    val minStock: Int,
    val expirationDate: LocalDate,
    val price: BigDecimal
)

data class PharmacyTask(
    val id: String,
    val title: String,
    val detail: String,
    val dueDate: LocalDate,
    val priority: TaskPriority,
    val category: String,
    val completed: Boolean = false
)

enum class TaskPriority(val label: String) {
    HIGH("Alta"),
    MEDIUM("Media"),
    LOW("Baja")
}

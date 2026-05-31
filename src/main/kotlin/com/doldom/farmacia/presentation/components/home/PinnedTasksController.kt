package com.doldom.farmacia.presentation.components.home

import com.doldom.farmacia.presentation.navigation.Navigator
import com.doldom.farmacia.presentation.state.AppSession
import com.doldom.farmacia.presentation.state.PharmacyTask
import javafx.animation.FadeTransition
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.util.Duration
import java.time.format.DateTimeFormatter

class PinnedTasksController {
    @FXML
    private lateinit var tasksContainer: VBox

    private val expandedLevels = mutableMapOf<String, Int>()
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM")

    @FXML
    private fun initialize() {
        AppSession.tasks.addListener(ListChangeListener { renderTasks() })
        renderTasks()
    }

    @FXML
    private fun onOpenCalendar() {
        Navigator.goToCalendario()
    }

    private fun renderTasks() {
        tasksContainer.children.setAll(
            AppSession.tasks
                .filter { !it.completed }
                .take(3)
                .map { createTaskCard(it) }
        )
    }

    private fun createTaskCard(task: PharmacyTask): HBox {
        val level = expandedLevels[task.id] ?: 0
        val card = HBox(14.0).apply {
            alignment = Pos.CENTER_LEFT
            styleClass.addAll("task-card", task.categoryStyle())
            setOnMouseClicked {
                expandedLevels[task.id] = (level + 1).coerceAtMost(2)
                renderTasks()
            }
        }

        val icon = Label(task.icon()).apply {
            styleClass.addAll("icon-mdl", "task-icon", task.textStyle())
        }

        val content = VBox(3.0).apply {
            HBox.setHgrow(this, javafx.scene.layout.Priority.ALWAYS)
            children.add(Label(task.title).apply { styleClass.add("task-title") })
            if (level >= 1) {
                children.add(Label(task.detail).apply {
                    styleClass.add("task-sub")
                    isWrapText = true
                })
            }
            if (level >= 2) {
                children.add(Label("${task.category.uppercase()} - ${task.priority.label} - ${task.dueDate.format(dateFormatter)}").apply {
                    styleClass.addAll("task-breakdown", task.textStyle())
                })
            }
        }

        val tag = Label(if (level == 0) "VER" else task.priority.label.uppercase()).apply {
            styleClass.addAll("task-tag", task.textStyle())
        }

        card.children.addAll(icon, content, Region().apply { HBox.setHgrow(this, javafx.scene.layout.Priority.ALWAYS) }, tag)
        FadeTransition(Duration.millis(180.0), card).apply {
            fromValue = 0.0
            toValue = 1.0
            play()
        }
        return card
    }

    private fun PharmacyTask.categoryStyle(): String = when (category.lowercase()) {
        "stock", "inventario" -> "stock"
        "cliente", "receta" -> "client"
        else -> "urgent"
    }

    private fun PharmacyTask.textStyle(): String = when (category.lowercase()) {
        "stock", "inventario" -> "stock-text"
        "cliente", "receta" -> "client-text"
        else -> "urgent-text"
    }

    private fun PharmacyTask.icon(): String = when (category.lowercase()) {
        "stock", "inventario" -> "\uE7BF"
        "cliente", "receta" -> "\uE8D6"
        else -> "!"
    }
}

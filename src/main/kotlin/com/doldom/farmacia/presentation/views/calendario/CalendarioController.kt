package com.doldom.farmacia.presentation.views.calendario

import com.doldom.farmacia.presentation.state.AppSession
import com.doldom.farmacia.presentation.state.PharmacyTask
import com.doldom.farmacia.presentation.state.TaskPriority
import javafx.animation.FadeTransition
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.util.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarioController {
    @FXML
    private lateinit var pendingTasksLabel: Label

    @FXML
    private lateinit var calendarTasksList: VBox

    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    @FXML
    private fun initialize() {
        AppSession.tasks.addListener(ListChangeListener { renderTasks() })
        renderTasks()
    }

    @FXML
    private fun onCreateTask() {
        createTaskDialog().showAndWait().ifPresent { form ->
            AppSession.addTask(
                title = form.title,
                detail = form.detail,
                dueDate = form.dueDate,
                priority = form.priority,
                category = form.category
            )
        }
    }

    private fun renderTasks() {
        val pending = AppSession.tasks.count { !it.completed }
        pendingTasksLabel.text = "$pending pendientes"

        calendarTasksList.children.setAll(
            AppSession.tasks.map { createTaskRow(it) }
        )
    }

    private fun createTaskRow(task: PharmacyTask): Node {
        val checkbox = CheckBox().apply {
            isFocusTraversable = false
            isMnemonicParsing = false
            isSelected = task.completed
            selectedProperty().addListener { _, _, selected -> AppSession.toggleTask(task, selected) }
        }

        val title = Label(task.title).apply {
            styleClass.add("calendar-task-title")
            if (task.completed) styleClass.add("done")
        }
        val sub = Label("${task.priority.label} - ${task.category} - ${task.dueDate.format(dateFormatter)}").apply {
            styleClass.add("calendar-task-sub")
        }
        val detail = Label(task.detail).apply {
            styleClass.add("calendar-task-sub")
            isWrapText = true
        }

        val content = VBox(2.0, title, sub, detail).apply {
            HBox.setHgrow(this, Priority.ALWAYS)
        }

        return HBox(12.0, checkbox, content).apply {
            alignment = Pos.CENTER_LEFT
            styleClass.add("calendar-task-row")
            FadeTransition(Duration.millis(180.0), this).apply {
                fromValue = 0.0
                toValue = 1.0
                play()
            }
        }
    }

    private fun createTaskDialog(): Dialog<TaskForm> {
        val titleField = TextField().apply { promptText = "Ej. Revisar medicamentos por vencer" }
        val detailField = TextArea().apply {
            promptText = "Notas, ubicacion, responsable o instrucciones"
            prefRowCount = 3
            isWrapText = true
        }
        val dueDatePicker = DatePicker(LocalDate.now())
        val priorityBox = ComboBox<TaskPriority>().apply {
            items.setAll(TaskPriority.entries)
            value = TaskPriority.MEDIUM
        }
        val categoryBox = ComboBox<String>().apply {
            items.setAll("Inventario", "Stock", "Cliente", "Receta", "Operativo")
            value = "Inventario"
            isEditable = true
        }

        val content = GridPane().apply {
            hgap = 14.0
            vgap = 12.0
            padding = Insets(8.0, 4.0, 4.0, 4.0)
            add(Label("Tarea").apply { styleClass.add("prescription-dialog-label") }, 0, 0)
            add(titleField, 1, 0)
            add(Label("Fecha").apply { styleClass.add("prescription-dialog-label") }, 0, 1)
            add(dueDatePicker, 1, 1)
            add(Label("Prioridad").apply { styleClass.add("prescription-dialog-label") }, 0, 2)
            add(priorityBox, 1, 2)
            add(Label("Categoria").apply { styleClass.add("prescription-dialog-label") }, 0, 3)
            add(categoryBox, 1, 3)
            add(Label("Detalle").apply { styleClass.add("prescription-dialog-label") }, 0, 4)
            add(detailField, 1, 4)
            columnConstraints.add(javafx.scene.layout.ColumnConstraints().apply { minWidth = 92.0 })
            columnConstraints.add(javafx.scene.layout.ColumnConstraints().apply {
                hgrow = Priority.ALWAYS
                minWidth = 360.0
            })
        }

        val createButton = ButtonType("Crear tarea", ButtonBar.ButtonData.OK_DONE)
        return Dialog<TaskForm>().apply {
            title = "Calendario"
            headerText = "Nueva tarea"
            dialogPane.content = content
            dialogPane.buttonTypes.addAll(createButton, ButtonType.CANCEL)
            dialogPane.stylesheets.add(
                CalendarioController::class.java.getResource("/com/doldom/farmacia/presentation/views/home/home.css")?.toExternalForm()
            )
            setResultConverter { type ->
                if (type != createButton || titleField.text.isBlank()) return@setResultConverter null
                TaskForm(
                    title = titleField.text.trim(),
                    detail = detailField.text.trim().ifBlank { "Sin detalle adicional" },
                    dueDate = dueDatePicker.value ?: LocalDate.now(),
                    priority = priorityBox.value ?: TaskPriority.MEDIUM,
                    category = categoryBox.value?.trim().orEmpty().ifBlank { "Operativo" }
                )
            }
        }
    }
}

private data class TaskForm(
    val title: String,
    val detail: String,
    val dueDate: LocalDate,
    val priority: TaskPriority,
    val category: String
)

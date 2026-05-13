package com.doldom.farmacia.core.utils

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty

abstract class BaseViewModel<T>(initialState: UiState<T> = UiState.Loading) {
    protected val _uiState: ObjectProperty<UiState<T>> = SimpleObjectProperty(initialState)
    val uiState: ObjectProperty<UiState<T>> = _uiState
}

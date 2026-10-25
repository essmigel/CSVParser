package com.masoud.csvviewer.presentation.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Base ViewModel for screens following the MVI pattern: the UI sends every user action as an
 * [Intent] to [handleIntent] and renders the single [uiState] it gets back.
 */
abstract class BaseViewModel<State, Intent>(initialState: State) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    abstract fun handleIntent(intent: Intent)

    protected fun updateState(reducer: State.() -> State) {
        _uiState.update(reducer)
    }
}

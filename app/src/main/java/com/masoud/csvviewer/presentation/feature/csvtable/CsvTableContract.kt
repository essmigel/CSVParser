package com.masoud.csvviewer.presentation.feature.csvtable

import com.masoud.csvviewer.presentation.common.UiText
import java.time.LocalDateTime

sealed interface CsvTableIntent {
    /** Loads the file, both when the screen opens and when the user retries after an error. */
    data object LoadTable : CsvTableIntent
}

sealed interface CsvTableUiState {
    data object Loading : CsvTableUiState
    data object Empty : CsvTableUiState
    data class Loaded(val rows: List<RowUi>) : CsvTableUiState
    data class Error(val message: UiText) : CsvTableUiState
}

data class RowUi(val fields: List<FieldUi>)

data class FieldUi(val label: String, val value: CellUi)

sealed interface CellUi {
    data class Text(val text: String) : CellUi
    data class Image(val url: String) : CellUi

    /** Formatted by the UI, so it follows the locale of the device. */
    data class Date(val dateTime: LocalDateTime) : CellUi
    data object Empty : CellUi
}

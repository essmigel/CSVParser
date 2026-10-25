package com.masoud.csvviewer.presentation.feature.csvtable

import com.masoud.csvviewer.R
import com.masoud.csvviewer.domain.model.CsvError
import com.masoud.csvviewer.domain.model.CsvTable
import com.masoud.csvviewer.domain.model.CsvValue
import com.masoud.csvviewer.domain.model.Result
import com.masoud.csvviewer.presentation.common.UiText
import javax.inject.Inject

class CsvTableUiMapper @Inject constructor() {

    fun toUiState(result: Result<CsvTable>): CsvTableUiState = when (result) {
        is Result.Success -> toUiState(result.data)
        is Result.Error -> CsvTableUiState.Error(toMessage(result.error))
    }

    private fun toUiState(table: CsvTable): CsvTableUiState {
        if (table.rows.isEmpty()) return CsvTableUiState.Empty

        val labels = table.columns.map { it.name }
        val rows = table.rows.map { row ->
            RowUi(fields = row.mapIndexed { index, value -> FieldUi(labels[index], toCell(value)) })
        }
        return CsvTableUiState.Loaded(rows)
    }

    private fun toCell(value: CsvValue): CellUi = when (value) {
        is CsvValue.Text -> if (value.value.isImageUrl()) {
            CellUi.Image(value.value)
        } else {
            CellUi.Text(value.value)
        }
        is CsvValue.Integer -> CellUi.Text(value.value.toString())
        is CsvValue.Date -> CellUi.Date(value.value)
        CsvValue.Empty -> CellUi.Empty
    }

    private fun toMessage(error: CsvError): UiText = when (error) {
        CsvError.Network -> UiText(R.string.error_network)
        is CsvError.MalformedFile -> UiText(R.string.error_malformed_file, listOf(error.line))
        is CsvError.InvalidRowSize -> UiText(
            R.string.error_invalid_row_size,
            listOf(error.row, error.valueCount, error.columnCount)
        )
    }

    private fun String.isImageUrl(): Boolean {
        val path = lowercase().substringBefore('?')
        val isWebUrl = path.startsWith("https://") || path.startsWith("http://")
        return isWebUrl && IMAGE_EXTENSIONS.any { path.endsWith(it) }
    }

    private companion object {
        val IMAGE_EXTENSIONS = listOf(".png", ".jpg", ".jpeg", ".gif", ".webp")
    }
}

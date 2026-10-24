package com.masoud.csvviewer.domain.model

data class CsvTable(
    val columns: List<Column>,
    val rows: List<List<CsvValue>>
) {
    init {
        require(rows.all { it.size == columns.size }) {
            "Every row must have the same number of cells as there are columns."
        }
    }
}

package com.masoud.csvviewer.domain.utils

import com.masoud.csvviewer.domain.model.Column
import com.masoud.csvviewer.domain.model.ColumnType
import com.masoud.csvviewer.domain.model.CsvTable
import com.masoud.csvviewer.domain.model.CsvValue
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import javax.inject.Inject

/**
 * Builds a [CsvTable] from raw CSV records, where the first record is the header.
 *
 * A column is an integer or a date column only when all of its non-blank values are; otherwise it
 * is a string column. Blank values become [CsvValue.Empty].
 *
 * Throws [InvalidRowSizeException] when a row does not have exactly as many values as the header.
 */
class CsvTableBuilder @Inject constructor() {

    fun build(records: List<List<String>>): CsvTable {
        if (records.isEmpty()) return CsvTable(columns = emptyList(), rows = emptyList())

        val header = records.first()
        val rows = records.drop(1)
        rows.forEachIndexed { index, row ->
            checkSize(row, size = header.size, rowNumber = index + 2)
        }

        val columns = header.mapIndexed { index, name ->
            Column(name = name, type = typeOf(rows.map { it[index] }))
        }
        val values = rows.map { row ->
            row.mapIndexed { index, value -> toValue(value, columns[index].type) }
        }
        return CsvTable(columns = columns, rows = values)
    }

    private fun checkSize(row: List<String>, size: Int, rowNumber: Int) {
        if (row.size != size) {
            throw InvalidRowSizeException(
                row = rowNumber,
                valueCount = row.size,
                columnCount = size
            )
        }
    }

    private fun typeOf(values: List<String>): ColumnType {
        val filledValues = values.filter { it.isNotBlank() }
        return when {
            filledValues.isEmpty() -> ColumnType.STRING
            filledValues.all { it.toIntegerOrNull() != null } -> ColumnType.INTEGER
            filledValues.all { it.toDateOrNull() != null } -> ColumnType.DATE
            else -> ColumnType.STRING
        }
    }

    private fun toValue(value: String, type: ColumnType): CsvValue {
        if (value.isBlank()) return CsvValue.Empty
        return when (type) {
            ColumnType.STRING -> CsvValue.Text(value)
            ColumnType.INTEGER -> CsvValue.Integer(value.toLong())
            ColumnType.DATE -> CsvValue.Date(checkNotNull(value.toDateOrNull()))
        }
    }

    /** Leading zeros and a plus sign are kept as text, so values like "007" are not changed. */
    private fun String.toIntegerOrNull(): Long? = toLongOrNull()?.takeIf { it.toString() == this }

    private fun String.toDateOrNull(): LocalDateTime? =
        try {
            LocalDateTime.parse(this)
        } catch (_: DateTimeParseException) {
            try {
                LocalDate.parse(this).atStartOfDay()
            } catch (_: DateTimeParseException) {
                null
            }
        }
}

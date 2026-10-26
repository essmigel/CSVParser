package com.masoud.csvviewer.domain.utils

import com.masoud.csvviewer.domain.model.Column
import com.masoud.csvviewer.domain.model.ColumnType
import com.masoud.csvviewer.domain.model.CsvError
import com.masoud.csvviewer.domain.model.CsvTable
import com.masoud.csvviewer.domain.model.CsvValue
import com.masoud.csvviewer.domain.model.Result
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import javax.inject.Inject

/**
 * Builds a [CsvTable] from raw CSV records, where the first record is the header.
 *
 * A column is an integer or a date column only when all of its non-blank values are; otherwise it
 * is a string column. Spaces around a value are ignored for integers and dates, and blank values
 * become [CsvValue.Empty].
 *
 * Returns [CsvError.InvalidRowSize] when a row does not have exactly as many values as the header.
 */
class CsvTableBuilder @Inject constructor() {

    fun build(records: List<List<String>>): Result<CsvTable> {
        if (records.isEmpty()) {
            return Result.Success(CsvTable(columns = emptyList(), rows = emptyList()))
        }

        val header = records.first()
        val rows = records.drop(1)
        rows.forEachIndexed { index, row ->
            if (row.size != header.size) {
                return Result.Error(
                    CsvError.InvalidRowSize(
                        row = index + 2,
                        valueCount = row.size,
                        columnCount = header.size
                    )
                )
            }
        }

        val columns = header.mapIndexed { index, name ->
            Column(name = name, type = typeOf(rows.map { it[index] }))
        }
        val values = rows.map { row ->
            row.mapIndexed { index, value -> toValue(value, columns[index].type) }
        }
        return Result.Success(CsvTable(columns = columns, rows = values))
    }

    private fun typeOf(values: List<String>): ColumnType {
        val filledValues = values.map { it.trim() }.filter { it.isNotEmpty() }
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
            ColumnType.INTEGER -> CsvValue.Integer(value.trim().toLong())
            ColumnType.DATE -> CsvValue.Date(checkNotNull(value.trim().toDateOrNull()))
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

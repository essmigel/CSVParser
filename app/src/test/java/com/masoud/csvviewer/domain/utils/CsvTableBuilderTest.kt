package com.masoud.csvviewer.domain.utils

import com.masoud.csvviewer.domain.model.Column
import com.masoud.csvviewer.domain.model.ColumnType
import com.masoud.csvviewer.domain.model.CsvTable
import com.masoud.csvviewer.domain.model.CsvValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDateTime

class CsvTableBuilderTest {

    private val builder = CsvTableBuilder()

    private fun typeOfColumn(vararg values: String): ColumnType =
        builder.build(listOf(listOf("Column")) + values.map { listOf(it) }).columns.single().type

    @Test
    fun `builds an empty table from no records`() {
        assertEquals(CsvTable(columns = emptyList(), rows = emptyList()), builder.build(emptyList()))
    }

    @Test
    fun `builds string columns without rows from a header only`() {
        val table = builder.build(listOf(listOf("Name", "Count")))

        assertEquals(
            listOf(Column("Name", ColumnType.STRING), Column("Count", ColumnType.STRING)),
            table.columns
        )
        assertEquals(emptyList<List<CsvValue>>(), table.rows)
    }

    @Test
    fun `detects an integer column`() {
        assertEquals(ColumnType.INTEGER, typeOfColumn("5", "-12", "0"))
    }

    @Test
    fun `detects a date column with date and time values`() {
        assertEquals(ColumnType.DATE, typeOfColumn("1978-01-02T00:00:00", "2001-04-20T13:45:00"))
    }

    @Test
    fun `detects a date column with date only values`() {
        assertEquals(ColumnType.DATE, typeOfColumn("1978-01-02", "2001-04-20T13:45:00"))
    }

    @Test
    fun `detects a string column when values have different types`() {
        assertEquals(ColumnType.STRING, typeOfColumn("5", "Theo"))
        assertEquals(ColumnType.STRING, typeOfColumn("5", "1978-01-02"))
    }

    @Test
    fun `keeps numbers with leading zeros or a plus sign as strings`() {
        assertEquals(ColumnType.STRING, typeOfColumn("0612345678"))
        assertEquals(ColumnType.STRING, typeOfColumn("+5"))
    }

    @Test
    fun `keeps decimal numbers and numbers that do not fit in a Long as strings`() {
        assertEquals(ColumnType.STRING, typeOfColumn("3.5"))
        assertEquals(ColumnType.STRING, typeOfColumn("9223372036854775808"))
    }

    @Test
    fun `keeps dates in other formats as strings`() {
        assertEquals(ColumnType.STRING, typeOfColumn("02-01-1978"))
    }

    @Test
    fun `ignores blank values when detecting the type`() {
        assertEquals(ColumnType.INTEGER, typeOfColumn("5", "", " "))
    }

    @Test
    fun `detects a string column when every value is blank`() {
        assertEquals(ColumnType.STRING, typeOfColumn("", ""))
    }

    @Test
    fun `converts blank values to Empty`() {
        val table = builder.build(listOf(listOf("Name", "Count"), listOf(" ", "")))

        assertEquals(listOf(listOf(CsvValue.Empty, CsvValue.Empty)), table.rows)
    }

    @Test
    fun `rejects a row with fewer values than columns`() {
        val records = listOf(listOf("Name", "Count"), listOf("Theo", "5"), listOf("Fiona"))

        val exception = assertThrows(InvalidRowSizeException::class.java) { builder.build(records) }

        assertEquals(3, exception.row)
        assertEquals(1, exception.valueCount)
        assertEquals(2, exception.columnCount)
    }

    @Test
    fun `rejects a row with more values than columns`() {
        val records = listOf(listOf("Name"), listOf("Theo", "5"))

        val exception = assertThrows(InvalidRowSizeException::class.java) { builder.build(records) }

        assertEquals(2, exception.row)
        assertEquals(2, exception.valueCount)
        assertEquals(1, exception.columnCount)
    }

    @Test
    fun `builds the assignment file`() {
        val records = listOf(
            listOf("First name", "Sur name", "Issue count", "Date of birth"),
            listOf("Theo", "Jansen", "5", "1978-01-02T00:00:00"),
            listOf("Fiona", "de Vries", "7", "1950-11-12T00:00:00")
        )

        val table = builder.build(records)

        assertEquals(
            CsvTable(
                columns = listOf(
                    Column("First name", ColumnType.STRING),
                    Column("Sur name", ColumnType.STRING),
                    Column("Issue count", ColumnType.INTEGER),
                    Column("Date of birth", ColumnType.DATE)
                ),
                rows = listOf(
                    listOf(
                        CsvValue.Text("Theo"),
                        CsvValue.Text("Jansen"),
                        CsvValue.Integer(5),
                        CsvValue.Date(LocalDateTime.of(1978, 1, 2, 0, 0))
                    ),
                    listOf(
                        CsvValue.Text("Fiona"),
                        CsvValue.Text("de Vries"),
                        CsvValue.Integer(7),
                        CsvValue.Date(LocalDateTime.of(1950, 11, 12, 0, 0))
                    )
                )
            ),
            table
        )
    }
}

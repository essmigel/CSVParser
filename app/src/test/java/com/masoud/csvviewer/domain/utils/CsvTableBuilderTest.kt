package com.masoud.csvviewer.domain.utils

import com.masoud.csvviewer.domain.model.Column
import com.masoud.csvviewer.domain.model.ColumnType
import com.masoud.csvviewer.domain.model.CsvError
import com.masoud.csvviewer.domain.model.CsvTable
import com.masoud.csvviewer.domain.model.CsvValue
import com.masoud.csvviewer.domain.model.Result
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class CsvTableBuilderTest {

    private val builder = CsvTableBuilder()

    private fun buildTable(records: List<List<String>>): CsvTable =
        (builder.build(records) as Result.Success).data

    private fun typeOfColumn(vararg values: String): ColumnType =
        buildTable(listOf(listOf("Column")) + values.map { listOf(it) }).columns.single().type

    @Test
    fun `builds an empty table from no records`() {
        val emptyTable = CsvTable(columns = emptyList(), rows = emptyList())

        assertEquals(emptyTable, buildTable(emptyList()))
    }

    @Test
    fun `builds string columns without rows from a header only`() {
        val table = buildTable(listOf(listOf("Name", "Count")))

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
    fun `ignores spaces around integers and dates but keeps them in text`() {
        val records = listOf(
            listOf("Name", "Count", "Date"),
            listOf(" Theo ", " 5", "1978-01-02 ")
        )

        assertEquals(
            listOf(
                listOf(
                    CsvValue.Text(" Theo "),
                    CsvValue.Integer(5),
                    CsvValue.Date(LocalDateTime.of(1978, 1, 2, 0, 0))
                )
            ),
            buildTable(records).rows
        )
    }

    @Test
    fun `detects a string column when every value is blank`() {
        assertEquals(ColumnType.STRING, typeOfColumn("", ""))
    }

    @Test
    fun `converts blank values to Empty`() {
        val table = buildTable(listOf(listOf("Name", "Count"), listOf(" ", "")))

        assertEquals(listOf(listOf(CsvValue.Empty, CsvValue.Empty)), table.rows)
    }

    @Test
    fun `rejects a row with fewer values than columns`() {
        val records = listOf(listOf("Name", "Count"), listOf("Theo", "5"), listOf("Fiona"))

        assertEquals(
            Result.Error(CsvError.InvalidRowSize(row = 3, valueCount = 1, columnCount = 2)),
            builder.build(records)
        )
    }

    @Test
    fun `rejects a row with more values than columns`() {
        val records = listOf(listOf("Name"), listOf("Theo", "5"))

        assertEquals(
            Result.Error(CsvError.InvalidRowSize(row = 2, valueCount = 2, columnCount = 1)),
            builder.build(records)
        )
    }

    @Test
    fun `builds the assignment file`() {
        val records = listOf(
            listOf("First name", "Sur name", "Issue count", "Date of birth"),
            listOf("Theo", "Jansen", "5", "1978-01-02T00:00:00"),
            listOf("Fiona", "de Vries", "7", "1950-11-12T00:00:00")
        )

        val table = buildTable(records)

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

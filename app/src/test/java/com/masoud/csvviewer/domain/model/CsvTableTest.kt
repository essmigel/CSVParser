package com.masoud.csvviewer.domain.model

import org.junit.Test

class CsvTableTest {

    private val columns = listOf(
        Column(name = "Name", type = ColumnType.STRING),
        Column(name = "Count", type = ColumnType.INTEGER)
    )

    @Test
    fun `accepts rows with one value per column`() {
        CsvTable(
            columns = columns,
            rows = listOf(
                listOf(CsvValue.Text("Theo"), CsvValue.Integer(5)),
                listOf(CsvValue.Text("Fiona"), CsvValue.Empty)
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects a row with fewer values than columns`() {
        CsvTable(columns = columns, rows = listOf(listOf(CsvValue.Text("Theo"))))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects a row with more values than columns`() {
        CsvTable(
            columns = columns,
            rows = listOf(listOf(CsvValue.Text("Theo"), CsvValue.Integer(5), CsvValue.Empty))
        )
    }
}

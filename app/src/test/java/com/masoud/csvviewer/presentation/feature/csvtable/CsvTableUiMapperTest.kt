package com.masoud.csvviewer.presentation.feature.csvtable

import com.masoud.csvviewer.R
import com.masoud.csvviewer.domain.model.Column
import com.masoud.csvviewer.domain.model.ColumnType
import com.masoud.csvviewer.domain.model.CsvError
import com.masoud.csvviewer.domain.model.CsvTable
import com.masoud.csvviewer.domain.model.CsvValue
import com.masoud.csvviewer.domain.model.Result
import com.masoud.csvviewer.presentation.common.UiText
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class CsvTableUiMapperTest {

    private val mapper = CsvTableUiMapper()

    private fun cellOf(value: CsvValue): CellUi {
        val table = CsvTable(
            columns = listOf(Column("Column", ColumnType.STRING)),
            rows = listOf(listOf(value))
        )
        val state = mapper.toUiState(Result.Success(table)) as CsvTableUiState.Loaded
        return state.rows.single().fields.single().value
    }

    @Test
    fun `maps every row to fields labelled with the column names`() {
        val table = CsvTable(
            columns = listOf(
                Column("Name", ColumnType.STRING),
                Column("Count", ColumnType.INTEGER)
            ),
            rows = listOf(
                listOf(CsvValue.Text("Theo"), CsvValue.Integer(5)),
                listOf(CsvValue.Text("Fiona"), CsvValue.Integer(7))
            )
        )

        assertEquals(
            CsvTableUiState.Loaded(
                listOf(
                    RowUi(
                        listOf(
                            FieldUi("Name", CellUi.Text("Theo")),
                            FieldUi("Count", CellUi.Text("5"))
                        )
                    ),
                    RowUi(
                        listOf(
                            FieldUi("Name", CellUi.Text("Fiona")),
                            FieldUi("Count", CellUi.Text("7"))
                        )
                    )
                )
            ),
            mapper.toUiState(Result.Success(table))
        )
    }

    @Test
    fun `maps a table without rows to Empty`() {
        val table = CsvTable(
            columns = listOf(Column("Name", ColumnType.STRING)),
            rows = emptyList()
        )

        assertEquals(CsvTableUiState.Empty, mapper.toUiState(Result.Success(table)))
    }

    @Test
    fun `keeps dates unformatted for the UI`() {
        val dateTime = LocalDateTime.of(1978, 1, 2, 0, 0)

        assertEquals(CellUi.Date(dateTime), cellOf(CsvValue.Date(dateTime)))
    }

    @Test
    fun `maps an empty value to Empty`() {
        assertEquals(CellUi.Empty, cellOf(CsvValue.Empty))
    }

    @Test
    fun `maps a web url to an image file to an image`() {
        val url = "https://api.multiavatar.com/2cdf5db9b4dee297b7.png"

        assertEquals(CellUi.Image(url), cellOf(CsvValue.Text(url)))
    }

    @Test
    fun `detects image urls with query parameters and upper case extensions`() {
        val url = "https://example.com/photo.JPG?size=large"

        assertEquals(CellUi.Image(url), cellOf(CsvValue.Text(url)))
    }

    @Test
    fun `keeps urls that are not images and image names that are not urls as text`() {
        val url = "https://example.com"

        assertEquals(CellUi.Text(url), cellOf(CsvValue.Text(url)))
        assertEquals(CellUi.Text("photo.png"), cellOf(CsvValue.Text("photo.png")))
    }

    @Test
    fun `maps a network error to its message`() {
        assertEquals(
            CsvTableUiState.Error(UiText(R.string.error_network)),
            mapper.toUiState(Result.Error(CsvError.Network))
        )
    }

    @Test
    fun `maps a malformed file error to a message with the line`() {
        assertEquals(
            CsvTableUiState.Error(UiText(R.string.error_malformed_file, listOf(4))),
            mapper.toUiState(Result.Error(CsvError.MalformedFile(line = 4)))
        )
    }

    @Test
    fun `maps an invalid row size error to a message with the row and counts`() {
        assertEquals(
            CsvTableUiState.Error(UiText(R.string.error_invalid_row_size, listOf(3, 1, 2))),
            mapper.toUiState(
                Result.Error(CsvError.InvalidRowSize(row = 3, valueCount = 1, columnCount = 2))
            )
        )
    }
}

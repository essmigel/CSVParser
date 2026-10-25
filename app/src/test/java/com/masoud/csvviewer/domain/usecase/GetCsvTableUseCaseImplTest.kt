package com.masoud.csvviewer.domain.usecase

import com.masoud.csvviewer.domain.model.Column
import com.masoud.csvviewer.domain.model.ColumnType
import com.masoud.csvviewer.domain.model.CsvError
import com.masoud.csvviewer.domain.model.CsvTable
import com.masoud.csvviewer.domain.model.CsvValue
import com.masoud.csvviewer.domain.model.Result
import com.masoud.csvviewer.domain.utils.CsvTableBuilder
import com.masoud.csvviewer.fakes.FakeCsvRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetCsvTableUseCaseImplTest {

    private val repository = FakeCsvRepository()

    private fun TestScope.useCase() = GetCsvTableUseCaseImpl(
        repository = repository,
        tableBuilder = CsvTableBuilder(),
        defaultDispatcher = StandardTestDispatcher(testScheduler)
    )

    @Test
    fun `returns the table built from the records`() = runTest {
        repository.result = Result.Success(listOf(listOf("Name", "Count"), listOf("Theo", "5")))

        assertEquals(
            Result.Success(
                CsvTable(
                    columns = listOf(
                        Column("Name", ColumnType.STRING),
                        Column("Count", ColumnType.INTEGER)
                    ),
                    rows = listOf(listOf(CsvValue.Text("Theo"), CsvValue.Integer(5)))
                )
            ),
            useCase()()
        )
    }

    @Test
    fun `returns the repository error`() = runTest {
        repository.result = Result.Error(CsvError.Network)

        assertEquals(Result.Error(CsvError.Network), useCase()())
    }

    @Test
    fun `returns an invalid row size error when a row does not match the header`() = runTest {
        repository.result = Result.Success(listOf(listOf("Name", "Count"), listOf("Theo")))

        assertEquals(
            Result.Error(CsvError.InvalidRowSize(row = 2, valueCount = 1, columnCount = 2)),
            useCase()()
        )
    }
}

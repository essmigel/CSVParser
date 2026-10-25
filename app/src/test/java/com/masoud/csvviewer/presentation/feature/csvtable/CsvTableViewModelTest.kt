package com.masoud.csvviewer.presentation.feature.csvtable

import com.masoud.csvviewer.R
import com.masoud.csvviewer.domain.model.Column
import com.masoud.csvviewer.domain.model.ColumnType
import com.masoud.csvviewer.domain.model.CsvError
import com.masoud.csvviewer.domain.model.CsvTable
import com.masoud.csvviewer.domain.model.CsvValue
import com.masoud.csvviewer.domain.model.Result
import com.masoud.csvviewer.fakes.FakeGetCsvTableUseCase
import com.masoud.csvviewer.presentation.common.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CsvTableViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val useCase = FakeGetCsvTableUseCase()

    private val table = CsvTable(
        columns = listOf(Column("Name", ColumnType.STRING)),
        rows = listOf(listOf(CsvValue.Text("Theo")))
    )
    private val loadedState = CsvTableUiState.Loaded(
        listOf(RowUi(listOf(FieldUi("Name", CellUi.Text("Theo")))))
    )
    private val networkErrorState = CsvTableUiState.Error(UiText(R.string.error_network))

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = CsvTableViewModel(useCase, CsvTableUiMapper())

    @Test
    fun `is loading until the table is loaded`() = runTest(dispatcher) {
        useCase.result = Result.Success(table)

        val viewModel = createViewModel()
        assertEquals(CsvTableUiState.Loading, viewModel.uiState.value)

        advanceUntilIdle()
        assertEquals(loadedState, viewModel.uiState.value)
    }

    @Test
    fun `shows an error when loading fails`() = runTest(dispatcher) {
        useCase.result = Result.Error(CsvError.Network)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(networkErrorState, viewModel.uiState.value)
    }

    @Test
    fun `loads the table again after an error`() = runTest(dispatcher) {
        useCase.result = Result.Error(CsvError.Network)
        val viewModel = createViewModel()
        advanceUntilIdle()

        useCase.result = Result.Success(table)
        viewModel.handleIntent(CsvTableIntent.LoadTable)
        assertEquals(CsvTableUiState.Loading, viewModel.uiState.value)

        advanceUntilIdle()
        assertEquals(loadedState, viewModel.uiState.value)
    }
}

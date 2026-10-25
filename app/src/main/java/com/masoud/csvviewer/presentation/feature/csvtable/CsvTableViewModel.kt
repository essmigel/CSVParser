package com.masoud.csvviewer.presentation.feature.csvtable

import androidx.lifecycle.viewModelScope
import com.masoud.csvviewer.domain.usecase.GetCsvTableUseCase
import com.masoud.csvviewer.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CsvTableViewModel @Inject constructor(
    private val getCsvTable: GetCsvTableUseCase,
    private val uiMapper: CsvTableUiMapper
) : BaseViewModel<CsvTableUiState, CsvTableIntent>(CsvTableUiState.Loading) {

    private var loadJob: Job? = null

    init {
        handleIntent(CsvTableIntent.LoadTable)
    }

    override fun handleIntent(intent: CsvTableIntent) {
        when (intent) {
            CsvTableIntent.LoadTable -> loadTable()
        }
    }

    private fun loadTable() {
        loadJob?.cancel()
        updateState { CsvTableUiState.Loading }
        loadJob = viewModelScope.launch {
            val result = getCsvTable()
            updateState { uiMapper.toUiState(result) }
        }
    }
}

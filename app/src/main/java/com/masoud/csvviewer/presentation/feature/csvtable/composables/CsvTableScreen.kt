package com.masoud.csvviewer.presentation.feature.csvtable.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.masoud.csvviewer.presentation.feature.csvtable.CsvTableViewModel

@Composable
fun CsvTableScreen(
    modifier: Modifier = Modifier,
    viewModel: CsvTableViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CsvTableContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
        modifier = modifier
    )
}

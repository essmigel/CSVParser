package com.masoud.csvviewer.presentation.feature.csvtable.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.masoud.csvviewer.R
import com.masoud.csvviewer.presentation.common.UiText
import com.masoud.csvviewer.presentation.feature.csvtable.CellUi
import com.masoud.csvviewer.presentation.feature.csvtable.CsvTableIntent
import com.masoud.csvviewer.presentation.feature.csvtable.CsvTableUiState
import com.masoud.csvviewer.presentation.feature.csvtable.FieldUi
import com.masoud.csvviewer.presentation.feature.csvtable.RowUi
import com.masoud.csvviewer.presentation.theme.CsvViewerTheme
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CsvTableContent(
    uiState: CsvTableUiState,
    onIntent: (CsvTableIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(text = stringResource(R.string.app_name)) }) }
    ) { innerPadding ->
        val stateModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (uiState) {
            CsvTableUiState.Loading -> LoadingContent(stateModifier)
            CsvTableUiState.Empty -> EmptyContent(stateModifier)
            is CsvTableUiState.Error -> ErrorContent(
                message = uiState.message,
                onRetry = { onIntent(CsvTableIntent.LoadTable) },
                modifier = stateModifier
            )
            is CsvTableUiState.Loaded -> RowList(rows = uiState.rows, innerPadding = innerPadding)
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier) {
    val description = stringResource(R.string.loading_file)
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = description }
        )
    }
}

@Composable
private fun EmptyContent(modifier: Modifier) {
    Box(modifier = modifier.padding(16.dp), contentAlignment = Alignment.Center) {
        Text(text = stringResource(R.string.empty_file), textAlign = TextAlign.Center)
    }
}

@Composable
private fun ErrorContent(message: UiText, onRetry: () -> Unit, modifier: Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.error_title),
            style = MaterialTheme.typography.titleMedium
        )
        Text(text = message.resolve(), textAlign = TextAlign.Center)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
            Text(text = stringResource(R.string.try_again))
        }
    }
}

/** Draws behind the system bars, but keeps the first and last card clear of them. */
@Composable
private fun RowList(rows: List<RowUi>, innerPadding: PaddingValues) {
    val layoutDirection = LocalLayoutDirection.current
    LazyColumn(
        contentPadding = PaddingValues(
            start = innerPadding.calculateStartPadding(layoutDirection) + 16.dp,
            top = innerPadding.calculateTopPadding() + 16.dp,
            end = innerPadding.calculateEndPadding(layoutDirection) + 16.dp,
            bottom = innerPadding.calculateBottomPadding() + 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(rows) { row -> RowCard(row = row) }
    }
}

@PreviewLightDark
@Composable
private fun LoadedPreview() {
    CsvViewerTheme {
        CsvTableContent(
            uiState = CsvTableUiState.Loaded(
                listOf(
                    RowUi(
                        listOf(
                            FieldUi("First name", CellUi.Text("Theo")),
                            FieldUi("Issue count", CellUi.Text("5")),
                            FieldUi(
                                "Date of birth",
                                CellUi.Date(LocalDateTime.of(1978, 1, 2, 0, 0))
                            ),
                            FieldUi("Nickname", CellUi.Empty)
                        )
                    )
                )
            ),
            onIntent = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun ErrorPreview() {
    CsvViewerTheme {
        CsvTableContent(
            uiState = CsvTableUiState.Error(UiText(R.string.error_network)),
            onIntent = {}
        )
    }
}

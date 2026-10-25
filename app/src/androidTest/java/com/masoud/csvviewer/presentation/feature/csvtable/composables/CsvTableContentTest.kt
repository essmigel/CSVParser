package com.masoud.csvviewer.presentation.feature.csvtable.composables

import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.masoud.csvviewer.R
import com.masoud.csvviewer.presentation.common.UiText
import com.masoud.csvviewer.presentation.feature.csvtable.CellUi
import com.masoud.csvviewer.presentation.feature.csvtable.CsvTableIntent
import com.masoud.csvviewer.presentation.feature.csvtable.CsvTableUiState
import com.masoud.csvviewer.presentation.feature.csvtable.FieldUi
import com.masoud.csvviewer.presentation.feature.csvtable.RowUi
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CsvTableContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun string(@StringRes resId: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)

    private fun setContent(uiState: CsvTableUiState, onIntent: (CsvTableIntent) -> Unit = {}) {
        composeRule.setContent { CsvTableContent(uiState = uiState, onIntent = onIntent) }
    }

    @Test
    fun showsALoadingIndicator() {
        setContent(CsvTableUiState.Loading)

        composeRule.onNodeWithContentDescription(string(R.string.loading_file)).assertExists()
    }

    @Test
    fun showsAMessageWhenTheFileHasNoRows() {
        setContent(CsvTableUiState.Empty)

        composeRule.onNodeWithText(string(R.string.empty_file)).assertExists()
    }

    @Test
    fun showsLabelsAndValuesOfEveryRow() {
        setContent(
            CsvTableUiState.Loaded(
                listOf(
                    RowUi(listOf(FieldUi("First name", CellUi.Text("Theo")))),
                    RowUi(listOf(FieldUi("First name", CellUi.Empty)))
                )
            )
        )

        composeRule.onNodeWithText("Theo").assertExists()
        composeRule.onNodeWithContentDescription(string(R.string.no_value)).assertExists()
    }

    @Test
    fun showsTheErrorAndRetriesOnTryAgain() {
        val intents = mutableListOf<CsvTableIntent>()
        setContent(CsvTableUiState.Error(UiText(R.string.error_network)), onIntent = intents::add)

        composeRule.onNodeWithText(string(R.string.error_network)).assertExists()
        composeRule.onNodeWithText(string(R.string.try_again)).performClick()

        assertEquals(listOf(CsvTableIntent.LoadTable), intents)
    }
}

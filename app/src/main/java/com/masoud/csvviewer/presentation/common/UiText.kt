package com.masoud.csvviewer.presentation.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * A string resource with its format arguments, so the ViewModel can describe a text without
 * needing a Context. The UI turns it into a String with [resolve].
 */
data class UiText(
    @StringRes val resId: Int,
    val args: List<Any> = emptyList()
) {
    @Composable
    fun resolve(): String = stringResource(resId, *args.toTypedArray())
}

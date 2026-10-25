package com.masoud.csvviewer.presentation.feature.csvtable.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.masoud.csvviewer.R
import com.masoud.csvviewer.presentation.feature.csvtable.CellUi
import com.masoud.csvviewer.presentation.feature.csvtable.FieldUi
import com.masoud.csvviewer.presentation.feature.csvtable.RowUi

/** Shows one CSV row as a card with a "label above value" line per column. */
@Composable
fun RowCard(row: RowUi, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {}
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            row.fields.forEach { field -> FieldItem(field) }
        }
    }
}

@Composable
private fun FieldItem(field: FieldUi) {
    Column {
        Text(
            text = field.label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        when (val cell = field.value) {
            is CellUi.Text -> ValueText(cell.text)
            is CellUi.Date -> {
                val locale = LocalConfiguration.current.locales[0]
                ValueText(formatDateTime(cell.dateTime, locale))
            }
            is CellUi.Image -> ImageValue(url = cell.url, description = field.label)
            CellUi.Empty -> EmptyValue()
        }
    }
}

@Composable
private fun ValueText(text: String, modifier: Modifier = Modifier) {
    Text(text = text, style = MaterialTheme.typography.bodyLarge, modifier = modifier)
}

@Composable
private fun EmptyValue() {
    val description = stringResource(R.string.no_value)
    ValueText(text = "–", modifier = Modifier.semantics { contentDescription = description })
}

/** Falls back to the url as text when the image cannot be loaded, so the value is never lost. */
@Composable
private fun ImageValue(url: String, description: String) {
    var failed by remember(url) { mutableStateOf(false) }
    if (failed) {
        ValueText(url)
    } else {
        AsyncImage(
            model = url,
            contentDescription = description,
            onError = { failed = true },
            modifier = Modifier
                .padding(top = 4.dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}

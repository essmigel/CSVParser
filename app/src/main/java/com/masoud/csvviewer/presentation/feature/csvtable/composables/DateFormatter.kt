package com.masoud.csvviewer.presentation.feature.csvtable.composables

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/** Formats in the style of [locale], leaving out the time when it is exactly midnight. */
fun formatDateTime(dateTime: LocalDateTime, locale: Locale): String {
    val formatter = if (dateTime.toLocalTime() == LocalTime.MIDNIGHT) {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    } else {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
    }
    return formatter.withLocale(locale).format(dateTime)
}

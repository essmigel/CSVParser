package com.masoud.csvviewer.presentation.feature.csvtable.composables

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.util.Locale

class DateFormatterTest {

    @Test
    fun `leaves out the time at midnight`() {
        val dateTime = LocalDateTime.of(1978, 1, 2, 0, 0)

        assertEquals("Jan 2, 1978", formatDateTime(dateTime, Locale.US))
    }

    @Test
    fun `includes the time when it is not midnight`() {
        val formatted = formatDateTime(LocalDateTime.of(2001, 4, 20, 13, 45), Locale.US)

        assertTrue(formatted, formatted.startsWith("Apr 20, 2001") && formatted.contains("1:45"))
    }

    @Test
    fun `follows the locale`() {
        val dateTime = LocalDateTime.of(1978, 1, 2, 0, 0)

        assertEquals("2 jan 1978", formatDateTime(dateTime, Locale.forLanguageTag("nl-NL")))
    }
}

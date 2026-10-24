package com.masoud.csvviewer.domain.model

import java.time.LocalDateTime

sealed interface CsvValue {
    data class Text(val value: String) : CsvValue
    data class Integer(val value: Long) : CsvValue
    data class Date(val value: LocalDateTime) : CsvValue
    data object Empty : CsvValue
}

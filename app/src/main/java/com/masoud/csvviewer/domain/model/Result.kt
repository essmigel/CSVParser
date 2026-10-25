package com.masoud.csvviewer.domain.model

sealed interface Result<out T> {
    data class Success<out T>(val data: T) : Result<T>
    data class Error(val error: CsvError) : Result<Nothing>
}

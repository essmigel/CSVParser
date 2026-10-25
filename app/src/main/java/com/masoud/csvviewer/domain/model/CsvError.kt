package com.masoud.csvviewer.domain.model

sealed interface CsvError {

    /** The file could not be downloaded, because of the connection or the server. */
    data object Network : CsvError

    /** The file is not valid CSV, for example a quoted value that is never closed. */
    data class MalformedFile(val line: Int) : CsvError

    /** A row does not have as many values as the header, where the header is row 1. */
    data class InvalidRowSize(val row: Int, val valueCount: Int, val columnCount: Int) : CsvError
}

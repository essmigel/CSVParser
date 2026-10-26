package com.masoud.csvviewer.domain.model

sealed interface CsvError {

    /** The server could not be reached, for example because the device is offline. */
    data object Network : CsvError

    /** The server was reached but did not return the file. */
    data class Server(val statusCode: Int) : CsvError

    /** The file is not valid CSV, for example a quoted value that is never closed. */
    data class MalformedFile(val line: Int) : CsvError

    /** A row does not have as many values as the header, where the header is row 1. */
    data class InvalidRowSize(val row: Int, val valueCount: Int, val columnCount: Int) : CsvError
}

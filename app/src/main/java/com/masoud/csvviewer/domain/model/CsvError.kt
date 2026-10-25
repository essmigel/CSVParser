package com.masoud.csvviewer.domain.model

sealed interface CsvError {

    /** The file could not be downloaded, because of the connection or the server. */
    data object Network : CsvError

    /** The file is not valid CSV, for example a quoted value that is never closed. */
    data class MalformedFile(val line: Int) : CsvError
}

package com.masoud.csvviewer.domain.repository

import com.masoud.csvviewer.domain.model.Result

interface CsvRepository {

    /** Returns every record of the CSV file as raw values, starting with the header. */
    suspend fun fetchRecords(): Result<List<List<String>>>
}

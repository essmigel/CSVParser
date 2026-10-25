package com.masoud.csvviewer.fakes

import com.masoud.csvviewer.domain.model.Result
import com.masoud.csvviewer.domain.repository.CsvRepository

class FakeCsvRepository(
    var result: Result<List<List<String>>> = Result.Success(emptyList())
) : CsvRepository {
    override suspend fun fetchRecords(): Result<List<List<String>>> = result
}

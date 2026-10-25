package com.masoud.csvviewer.fakes

import com.masoud.csvviewer.domain.model.CsvError
import com.masoud.csvviewer.domain.model.CsvTable
import com.masoud.csvviewer.domain.model.Result
import com.masoud.csvviewer.domain.usecase.GetCsvTableUseCase

class FakeGetCsvTableUseCase(
    var result: Result<CsvTable> = Result.Error(CsvError.Network)
) : GetCsvTableUseCase {
    override suspend fun invoke(): Result<CsvTable> = result
}

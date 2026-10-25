package com.masoud.csvviewer.domain.usecase

import com.masoud.csvviewer.domain.model.CsvTable
import com.masoud.csvviewer.domain.model.Result

interface GetCsvTableUseCase {
    suspend operator fun invoke(): Result<CsvTable>
}

package com.masoud.csvviewer.domain.usecase

import com.masoud.csvviewer.di.DefaultDispatcher
import com.masoud.csvviewer.domain.model.CsvError
import com.masoud.csvviewer.domain.model.CsvTable
import com.masoud.csvviewer.domain.model.Result
import com.masoud.csvviewer.domain.repository.CsvRepository
import com.masoud.csvviewer.domain.utils.CsvTableBuilder
import com.masoud.csvviewer.domain.utils.InvalidRowSizeException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Fetches the raw CSV records and turns them into a typed [CsvTable]. Building the table is CPU
 * work, so it runs on the [defaultDispatcher].
 */
class GetCsvTableUseCaseImpl @Inject constructor(
    private val repository: CsvRepository,
    private val tableBuilder: CsvTableBuilder,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : GetCsvTableUseCase {

    override suspend fun invoke(): Result<CsvTable> =
        when (val result = repository.fetchRecords()) {
            is Result.Success -> withContext(defaultDispatcher) { buildTable(result.data) }
            is Result.Error -> result
        }

    private fun buildTable(records: List<List<String>>): Result<CsvTable> =
        try {
            Result.Success(tableBuilder.build(records))
        } catch (e: InvalidRowSizeException) {
            Result.Error(CsvError.InvalidRowSize(e.row, e.valueCount, e.columnCount))
        }
}

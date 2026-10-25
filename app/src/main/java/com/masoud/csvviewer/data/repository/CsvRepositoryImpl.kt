package com.masoud.csvviewer.data.repository

import com.masoud.csvparser.CsvParseException
import com.masoud.csvparser.CsvParser
import com.masoud.csvviewer.di.CsvUrl
import com.masoud.csvviewer.di.IoDispatcher
import com.masoud.csvviewer.domain.model.CsvError
import com.masoud.csvviewer.domain.model.Result
import com.masoud.csvviewer.domain.repository.CsvRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.coroutines.executeAsync
import java.io.IOException
import javax.inject.Inject

/**
 * Downloads the CSV file from [url] and parses it while it is being received, so the file is never
 * stored on the device.
 */
class CsvRepositoryImpl @Inject constructor(
    private val client: OkHttpClient,
    private val parser: CsvParser,
    @CsvUrl private val url: String,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CsvRepository {

    override suspend fun fetchRecords(): Result<List<List<String>>> = withContext(ioDispatcher) {
        val request = Request.Builder().url(url).build()
        try {
            client.newCall(request).executeAsync().use { response ->
                if (response.isSuccessful) {
                    Result.Success(parser.parse(response.body.charStream()))
                } else {
                    Result.Error(CsvError.Network)
                }
            }
        } catch (_: IOException) {
            Result.Error(CsvError.Network)
        } catch (e: CsvParseException) {
            Result.Error(CsvError.MalformedFile(line = e.line))
        }
    }
}

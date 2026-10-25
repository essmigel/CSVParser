package com.masoud.csvviewer.data.repository

import com.masoud.csvparser.CsvParser
import com.masoud.csvviewer.domain.model.CsvError
import com.masoud.csvviewer.domain.model.Result
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CsvRepositoryImplTest {

    private val server = MockWebServer()

    @Before
    fun setUp() {
        server.start()
    }

    @After
    fun tearDown() {
        server.close()
    }

    private fun TestScope.repository() = CsvRepositoryImpl(
        client = OkHttpClient(),
        parser = CsvParser(),
        url = server.url("/issues.csv").toString(),
        ioDispatcher = StandardTestDispatcher(testScheduler)
    )

    @Test
    fun `returns the records of the downloaded file`() = runTest {
        server.enqueue(MockResponse(body = "\"First name\",\"Issue count\"\r\n\"Zoë\",5"))

        val result = repository().fetchRecords()

        assertEquals(
            Result.Success(listOf(listOf("First name", "Issue count"), listOf("Zoë", "5"))),
            result
        )
    }

    @Test
    fun `returns a network error when the server responds with an error`() = runTest {
        server.enqueue(MockResponse(code = 500))

        assertEquals(Result.Error(CsvError.Network), repository().fetchRecords())
    }

    @Test
    fun `returns a network error when the server cannot be reached`() = runTest {
        val repository = repository()
        server.close()

        assertEquals(Result.Error(CsvError.Network), repository.fetchRecords())
    }

    @Test
    fun `returns a malformed file error with the line of the problem`() = runTest {
        server.enqueue(MockResponse(body = "a,b\n\"never closed,c"))

        assertEquals(Result.Error(CsvError.MalformedFile(line = 2)), repository().fetchRecords())
    }
}

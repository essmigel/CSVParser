package com.masoud.csvparser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CsvParserTest {

    private fun parse(csv: String): List<List<String>> = CsvParser().parse(csv.reader())

    @Test
    fun `parses unquoted fields`() {
        assertEquals(listOf(listOf("a", "b", "c")), parse("a,b,c"))
    }

    @Test
    fun `parses quoted fields`() {
        assertEquals(listOf(listOf("a", "b")), parse("\"a\",\"b\""))
    }

    @Test
    fun `parses a mix of quoted and unquoted fields`() {
        assertEquals(listOf(listOf("Theo", "5")), parse("\"Theo\",5"))
    }

    @Test
    fun `keeps commas inside quoted fields`() {
        assertEquals(listOf(listOf("Doe, John", "x")), parse("\"Doe, John\",x"))
    }

    @Test
    fun `turns two double quotes inside a quoted field into one`() {
        assertEquals(listOf(listOf("say \"hi\"")), parse("\"say \"\"hi\"\"\""))
    }

    @Test
    fun `keeps line breaks inside quoted fields`() {
        assertEquals(listOf(listOf("line 1\r\nline 2", "x")), parse("\"line 1\r\nline 2\",x"))
    }

    @Test
    fun `splits records on LF, CRLF and CR`() {
        assertEquals(
            listOf(listOf("a"), listOf("b"), listOf("c"), listOf("d")),
            parse("a\nb\r\nc\rd")
        )
    }

    @Test
    fun `does not create a record for a trailing line break`() {
        assertEquals(listOf(listOf("a"), listOf("b")), parse("a\r\nb\r\n"))
    }

    @Test
    fun `parses empty fields`() {
        assertEquals(listOf(listOf("", "b", "")), parse(",b,"))
    }

    @Test
    fun `parses an empty quoted field`() {
        assertEquals(listOf(listOf("", "b")), parse("\"\",b"))
    }

    @Test
    fun `keeps spaces around values`() {
        assertEquals(listOf(listOf(" a ", " b")), parse(" a , b"))
    }

    @Test
    fun `keeps a double quote inside an unquoted field`() {
        assertEquals(listOf(listOf("5\" screen")), parse("5\" screen"))
    }

    @Test
    fun `skips a leading byte order mark`() {
        assertEquals(listOf(listOf("a")), parse("\uFEFFa"))
    }

    @Test
    fun `skips blank lines`() {
        assertEquals(listOf(listOf("a"), listOf("b")), parse("\r\na\n\n\r\nb\n"))
    }

    @Test
    fun `returns no records for empty input`() {
        assertEquals(emptyList<List<String>>(), parse(""))
    }

    @Test
    fun `reports the line where an unclosed quoted field starts`() {
        val exception = assertThrows(CsvParseException::class.java) {
            parse("a\n\"b\nc")
        }

        assertEquals(2, exception.line)
    }

    @Test
    fun `reports a character after a closing quote`() {
        val exception = assertThrows(CsvParseException::class.java) {
            parse("a\n\"b\"c")
        }

        assertEquals(2, exception.line)
    }

    @Test
    fun `counts line breaks inside quoted fields when reporting lines`() {
        val exception = assertThrows(CsvParseException::class.java) {
            parse("\"a\r\nb\",x\r\n\"c\"d")
        }

        assertEquals(3, exception.line)
    }

    @Test
    fun `parses the assignment file`() {
        val csv = "\"First name\",\"Sur name\",\"Issue count\",\"Date of birth\"\r\n" +
            "\"Theo\",\"Jansen\",5,\"1978-01-02T00:00:00\"\r\n" +
            "\"Fiona\",\"de Vries\",7,\"1950-11-12T00:00:00\""

        assertEquals(
            listOf(
                listOf("First name", "Sur name", "Issue count", "Date of birth"),
                listOf("Theo", "Jansen", "5", "1978-01-02T00:00:00"),
                listOf("Fiona", "de Vries", "7", "1950-11-12T00:00:00")
            ),
            parse(csv)
        )
    }
}

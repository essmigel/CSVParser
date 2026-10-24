package com.masoud.csvparser

import java.io.Reader

/**
 * Splits CSV text into records, where every record is the list of its raw field values.
 *
 * A field wrapped in double quotes can contain commas, line breaks and double quotes, which are
 * written twice (`""`). Records can end with CRLF, LF or CR, blank lines are skipped and a leading
 * byte order mark is ignored.
 *
 * Throws [CsvParseException] when a quoted field is never closed or is followed by anything other
 * than a comma or a line break.
 */
class CsvParser {

    fun parse(reader: Reader): List<List<String>> {
        val recordReader = RecordReader(reader.buffered())
        val records = mutableListOf<List<String>>()
        while (true) {
            records += recordReader.nextRecord() ?: break
        }
        return records
    }

    private class RecordReader(private val reader: Reader) {

        /** The character being looked at, or `null` once the input is exhausted. */
        private var current: Char? = null
        private var line = 1

        init {
            advance()
            if (current == BYTE_ORDER_MARK) advance()
        }

        fun nextRecord(): List<String>? {
            while (isAtLineBreak()) advance()
            if (current == null) return null

            val record = mutableListOf(readField())
            while (current == ',') {
                advance()
                record += readField()
            }

            skipLineBreak()
            return record
        }

        private fun readField(): String =
            if (current == '"') readQuotedField() else readUnquotedField()

        private fun readUnquotedField(): String {
            val field = StringBuilder()
            while (!isAtFieldEnd()) {
                field.append(current)
                advance()
            }
            return field.toString()
        }

        private fun readQuotedField(): String {
            val startLine = line
            val field = StringBuilder()
            advance()

            while (true) {
                when (current) {
                    null -> throw CsvParseException(startLine, "Quoted field is never closed")
                    '"' -> {
                        advance()
                        if (current != '"') break
                        field.append('"')
                        advance()
                    }
                    else -> {
                        field.append(current)
                        advance()
                    }
                }
            }

            if (!isAtFieldEnd()) {
                throw CsvParseException(line, "Unexpected character after closing quote")
            }
            return field.toString()
        }

        private fun skipLineBreak() {
            if (current == '\r') advance()
            if (current == '\n') advance()
        }

        private fun isAtLineBreak() = current == '\r' || current == '\n'

        private fun isAtFieldEnd() = current == ',' || current == null || isAtLineBreak()

        private fun advance() {
            val previous = current
            current = reader.read().takeIf { it != -1 }?.toChar()
            if (previous == '\n' || (previous == '\r' && current != '\n')) line++
        }
    }

    private companion object {
        const val BYTE_ORDER_MARK = '\uFEFF'
    }
}

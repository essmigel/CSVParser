package com.masoud.csvparser

class CsvParseException(val line: Int, reason: String) : Exception("Line $line: $reason")

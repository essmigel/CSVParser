package com.masoud.csvviewer.domain.utils

/**
 * @property row The position of the row in the file, where the header is row 1.
 */
class InvalidRowSizeException(
    val row: Int,
    val valueCount: Int,
    val columnCount: Int
) : Exception("Row $row has $valueCount values but there are $columnCount columns")

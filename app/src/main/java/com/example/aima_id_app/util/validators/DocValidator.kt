package com.example.aima_id_app.util.validators

import java.io.File
import java.io.FileInputStream
import java.io.IOException

class DocValidator {

    private val MAX_FILE_SIZE = 5 * 1024 * 1024 // 5 MB in bytes

    // Method to validate if the file is a PDF and within the size limit
    fun validatePDF(document: File): String {
        if (!document.exists() || !document.isFile) {
            return "Invalid document: The file does not exist or is not a valid file."
        }

        if (!isPDF(document)) {
            return "Invalid document: The file is not a PDF."
        }

        if (!isWithinSizeLimit(document)) {
            return "Invalid document: The file exceeds the maximum size of 5 MB."
        }

        return "The document is a valid PDF and meets the size requirements."
    }

    // Helper function to check if the file has PDF's magic number
    private fun isPDF(file: File): Boolean {
        try {
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(4)
                if (fis.read(buffer) == 4) {
                    // PDF files start with "%PDF"
                    return buffer[0] == 0x25.toByte() &&
                            buffer[1] == 0x50.toByte() &&
                            buffer[2] == 0x44.toByte() &&
                            buffer[3] == 0x46.toByte()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    // Helper function to check if the file size is within the acceptable limit
    private fun isWithinSizeLimit(file: File): Boolean {
        return file.length() <= MAX_FILE_SIZE
    }

}
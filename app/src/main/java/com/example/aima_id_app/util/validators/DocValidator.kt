package com.example.aima_id_app.util.validators

import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * A class responsible for validating PDF document files.
 *
 * This class provides methods to verify that a document is a valid PDF file
 * and meets specific criteria, such as file format and size limits.
 */
class DocValidator {

    private val MAX_FILE_SIZE = 5 * 1024 * 1024

    /**
     * Validates whether the provided document is a valid PDF file.
     *
     * This method checks that the document:
     * - Exists and is a file.
     * - Is in PDF format (starting with "%PDF").
     * - Is within the specified size limit (5 MB).
     *
     * @param document the file to validate.
     * @return a string indicating the validation result.
     */
    fun validatePDF(document: File): String {
        if (!document.exists() || !document.isFile) {
            return "Document does not exist or is not a file."
        }

        if (!isPDF(document)) {
            return "Document is not a PDF file."
        }

        if (!isWithinSizeLimit(document)) {
            return "Document exceeds the maximum allowed size of 5 MB."
        }

        return "Document is a valid PDF."
    }

    /**
     * Checks if the provided file is in PDF format.
     *
     * This method reads the first four bytes of the file to verify
     * it starts with the "%PDF" signature, indicating a PDF format.
     *
     * @param file the file to check.
     * @return `true` if the file is a PDF, `false` otherwise.
     */
    private fun isPDF(file: File): Boolean {
        try {
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(4)
                if (fis.read(buffer) == 4) {

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

    /**
     * Checks if the provided file is within the allowed size limit.
     *
     * @param file the file to check.
     * @return `true` if the file size is within 5 MB, `false` otherwise.
     */
    private fun isWithinSizeLimit(file: File): Boolean {
        return file.length() <= MAX_FILE_SIZE
    }
}

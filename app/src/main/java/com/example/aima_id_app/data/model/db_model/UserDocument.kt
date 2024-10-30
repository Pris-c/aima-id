package com.example.aima_id_app.data.model.db_model

import com.example.aima_id_app.util.enums.DocStatus
import com.example.aima_id_app.util.enums.DocType
import java.time.LocalDate

/**
 * Represents a User's document with relevant information.
 *
 * @property docPath The file path of the document.
 * @property docType The type of the document, defined by [DocType].
 * @property userId The unique identifier of the user associated with this document.
 * @property status The status of the document, defined by [DocStatus].
 * @property submittedAt The date when the document was submitted.
 * @property expirationDate The expiration date of the document. Default is set to December 30, 2125.
 */

data class UserDocument (
    val docPath: String,
    val docType: DocType,
    val userId: String,
    val status: DocStatus,
    val submittedAt: LocalDate,
    val expirationDate: LocalDate = LocalDate.of(2125,12,30)
)

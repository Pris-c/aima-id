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
 * @property expirationDate The expiration date of the document.
 */

data class UserDocument (
    var docPath: String = "",
    val docType: String = DocType.NIF.toString(),
    val userId: String = "",
    var status: String = DocStatus.SUBMITTED.toString(),
    var submittedAt: String = LocalDate.of(1,1,1).toString(),
    var expirationDate: String = ""
)

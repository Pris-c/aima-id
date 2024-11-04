package com.example.aima_id_app.data.model.submodel

import com.example.aima_id_app.util.enums.DocType

/**
 * Represents a document associated with a user.
 *
 * @property docType The type of the document (e.g., Passport, ID Card).
 * @property docId The unique identifier of the document.
 *
 */
class DocUser (
    // TODO Update to String
    val docType: DocType = DocType.NIF,
    val docId: String = ""
)
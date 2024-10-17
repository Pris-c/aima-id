package com.example.aima_id_app.data.model.submodel

import util.docType

/**
 * Represents a document associated with a user.
 *
 * @property docType The type of the document (e.g., Passport, ID Card).
 * @property docId The unique identifier of the document.
 *
 */
class DocUser (val docType: docType, val docId: String) {

    /**
     * Displays the details of the user's document.
     */
    fun displayDetails(){
        println("Document Type: $docType, Document ID: $docId")
    }
}
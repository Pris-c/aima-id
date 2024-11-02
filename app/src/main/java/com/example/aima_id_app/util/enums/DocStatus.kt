package com.example.aima_id_app.util.enums


/**
 * Enum class representing the status of a document.
 * This can be used to track the lifecycle stage of a user's document.
 *
 * @property SUBMITTED The document has been submitted and is awaiting review.
 * @property APPROVED The document has been reviewed and approved.
 * @property REJECTED The document has been reviewed and rejected.
 * @property EXPIRED The document is no longer valid due to expiration.
 */

enum class DocStatus(val status: String) {
        SUBMITTED("Submetido"),
        APPROVED("Aprovado"),
        REJECTED("Rejeitado"),
        EXPIRED("Expirado")
}
package com.example.aima_id_app.util.enums


/**
 * Enum class representing the status of a document.
 * This can be used to track the lifecycle stage of a user's document.
 *
 * @property status The name of the document status in Portuguese.
 */
enum class DocStatus(val status: String) {
        SUBMITTED("Submetido"),
        APPROVED("Aprovado"),
        REJECTED("Rejeitado"),
        EXPIRED("Expirado");

        companion object {
                fun fromStatus(status: String): DocStatus? {
                        return DocStatus.values().find { it.status.equals(status, ignoreCase = true) }
                }
        }
}
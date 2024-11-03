package com.example.aima_id_app.util.enums


/**
 * Enum class representing different types of documents.
 * These document types can be used to categorize or identify various documents a user may possess.
 */
enum class DocType(val doc: String) {
    PASSPORT("Passaporte"),
    VISA("Visto"),
    RESIDENT_PERMIT("Autorização de Residência"),
    ADDRESS_PROOF("Comprovativo de morada"),
    EMPLOYMENT_CONTRACT("Contrato de Trabalho"),
    CRIMINAL_RECORD("Registro Criminal"),
    SCHOOL_REGISTRATION("Matricula Escolar"),
    HEALTH_INSURANCE("Seguro Saúde"),
    NIF("NIF"),
    NISS("NISS"),
    CITIZENSHIP_ID("Atestado de Nacionalidade"),
    PROOF_INCOME("Comprovativo de Meios de Subsistência")
}
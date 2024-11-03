package com.example.aima_id_app.util.enums


/**
 * Enum class representing various document types required for identification and verification purposes.
 *
 * Each enum constant corresponds to a specific document type, providing its name in Portuguese.
 *
 * @property doc The name of the document in Portuguese.
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
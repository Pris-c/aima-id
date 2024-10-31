package com.example.aima_id_app.util.enums

/**
 * Enum class representing the status of a process.
 *
 * @property status A string that describes the status in Portuguese.
 *
 */
enum class ProcessStatus(val status: String) {

    IN_ANALYSIS("Em análise"), APPROVED("Aprovado"), REFUSED("Recusado")
}
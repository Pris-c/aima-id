package com.example.aima_id_app.data.model.db_model

import com.example.aima_id_app.util.enums.ProcessStatus
import java.time.LocalDate

/**
 * Data class representing an Aima process.
 *
 * @property userId The ID of the user associated with the process.
 * @property serviceCode The code of the service related to the process.
 * @property status The current status of the process (default is IN_ANALYSIS).
 * @property createdAt The date the process was created (default is the current date).
 * @property updatedAt The date the process was last updated (default is the current date).
 *
 * This class holds information about a specific process in the Aima system.
 */
data class AimaProcess (
    val userId: String = "",
    val serviceCode: String = "",
    var status: ProcessStatus = ProcessStatus.IN_ANALYSIS,
    val createdAt: String = LocalDate.now().toString(),
    var updatedAt: String = LocalDate.now().toString()
){
}
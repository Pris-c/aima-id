package com.example.aima_id_app.data.model.db_model

import com.example.aima_id_app.util.enums.PossibleScheduling

/**
 * Represents an appointment in the application.
 *
 * @property userId The ID of the user associated with the appointment.
 * @property processId The ID of the process related to the appointment.
 * @property aimaUnitId The ID of the AIMA unit where the appointment takes place.
 * @property date The date of the appointment in YYYY-MM-DD format.
 * @property time The time of the appointment. Default is `PossibleScheduling.TIME_09_00`.
 *
 */
data class Appointment (
    val userId: String ="",
    val processId: String = "",
    val aimaUnitId: String = "",
    val date: String = "",
    val time: PossibleScheduling = PossibleScheduling.TIME_09_00,
) {
}
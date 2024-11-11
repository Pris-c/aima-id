package com.example.aima_id_app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.AimaUnit
import com.example.aima_id_app.data.model.db_model.Appointment
import com.example.aima_id_app.data.repository.AimaUnitRepository
import com.example.aima_id_app.data.repository.AppointmentRepository
import com.example.aima_id_app.util.enums.PossibleScheduling
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate

class SchedulingViewModel(
    private val appointmentRepository: AppointmentRepository = AppointmentRepository(),
    private val aimaUnitRepository: AimaUnitRepository = AimaUnitRepository()
): ViewModel() {


    /**
     * Maps available appointment times to city units.
     *
     * This function retrieves available appointment times for each unit in a given city on a specific day.
     * It uses the `aimaUnitRepository` to get the units in the city and then calls `getAvailableDayTime`
     * for each unit to determine the available times. The results are then passed to the `onComplete` callback
     * as a map of AimaUnit to a list of PossibleScheduling.
     *
     * @param city The city to retrieve units for.
     * @param day The date to check for available times.
     * @param onComplete A callback function that is invoked with the map of available times by unit.
     */
    fun mapAvailableTimeByCityUnits(
        city: String,
        day: LocalDate,
        onComplete: (MutableMap<AimaUnit, MutableList<PossibleScheduling>>) -> Unit
    ) {
        val availableTimeByUnit = mutableMapOf<AimaUnit, MutableList<PossibleScheduling>>()

        aimaUnitRepository.getAimaUnitsByCity(city) { aimaUnits ->
            if (aimaUnits.isNotEmpty()) {
                val totalUnits = aimaUnits.size
                var processedUnits = 0

                for ((id, unit) in aimaUnits) {
                    getAvailableDayTime(id, unit, day) { timesList ->
                        availableTimeByUnit[unit] = timesList
                        Log.d("DEBUG", "Unit $id has ${timesList.size} available time")

                        processedUnits++

                        if (processedUnits == totalUnits) {
                            onComplete(availableTimeByUnit)
                        }
                    }
                }
            } else {
                Log.d("DEBUG", "No aimaUnits found in $city")
                onComplete(availableTimeByUnit)
            }
        }
    }

    /**
     * Retrieves available appointment times for a specific unit on a given day.
     *
     * This function calculates the available appointment times for a unit by comparing the existing
     * appointments with the total staff capacity of the unit. It uses the `appointmentRepository`
     * to retrieve the appointments for the unit on the specified day. The available times are
     * then passed to the `onComplete` callback as a list of PossibleScheduling.
     *
     * @param aimaUnitId The ID of the unit.
     * @param aimaUnit The AimaUnit object.
     * @param day The date to check for available times.
     * @param onComplete A callback function that is invoked with the list of available times.
     */
    private fun getAvailableDayTime(aimaUnitId: String, aimaUnit: AimaUnit, day: LocalDate,
                                    onComplete: (MutableList<PossibleScheduling>) -> Unit
    ) {
        val availableTimes = mutableListOf<PossibleScheduling>()
        appointmentRepository.findDayAppointmentOfUnit(aimaUnitId, day.toString()){ dayAppointments ->


            val staff = aimaUnit.staffIds.size
            Log.d("DEBUG", "Unit ${aimaUnitId} has $staff staffs")

            for (time in PossibleScheduling.entries) {
                val countAppointment = dayAppointments.filter { it.time == time.time }.size
                Log.d("DEBUG", "Unit ${aimaUnitId} has $countAppointment for time $time")

                if (countAppointment < staff){
                    availableTimes.add(time)
                }

            }
            onComplete(availableTimes)
        }
    }

    /**
     * Saves an appointment to the repository.
     *
     * This function creates a new Appointment object and saves it to the repository using the
     * `appointmentRepository`. The `onComplete` callback is invoked with a boolean value indicating
     * the success or failure of the save operation.
     *
     * @param processId The ID of the process associated with the appointment.
     * @param aimaUnitId The ID of the unit where the appointment is scheduled.
     * @param date The date of the appointment.
     * @param time The time of the appointment.
     * @param onComplete A callback function that is invoked with the result of the save operation.
     */
    fun saveAppointment(processId: String, aimaUnitId: String, date: LocalDate, time: String, onComplete: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: run {
            onComplete(false)
            return
        }

        val newAppointment = Appointment(
            userId = userId,
            processId = processId,
            aimaUnitId = aimaUnitId,
            date = date.toString(),
            time = time
        )

        appointmentRepository.createAppointment(newAppointment) { success ->
            onComplete(success)
        }
    }


}
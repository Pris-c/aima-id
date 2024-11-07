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
/*
    fun saveAppointment(processId: String, aimaUnitId: String, date: LocalDate, time: PossibleScheduling,
                        onComplete: (Boolean) -> Unit){
        val newAppointment = Appointment(processId, aimaUnitId, date.toString(), time.time)
        appointmentRepository.createAppointment(newAppointment){ success ->
            onComplete(success)
        }
    }*/

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
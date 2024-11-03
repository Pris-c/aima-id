package com.example.aima_id_app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.AimaUnit
import com.example.aima_id_app.data.repository.AimaUnitRepository
import com.example.aima_id_app.data.repository.AppointmentRepository
import com.example.aima_id_app.util.enums.PossibleScheduling
import java.time.LocalDate

class SchedulingViewModel(
    private val appointmentRepository: AppointmentRepository = AppointmentRepository(),
    private val aimaUnitRepository: AimaUnitRepository = AimaUnitRepository()
): ViewModel() {



    fun mapAvailableTimeByCityUnits(
        city: String,
        day: LocalDate,
        onComplete: (MutableMap<String, MutableList<PossibleScheduling>>) -> Unit
    ) {
        val availableTimeByUnit = mutableMapOf<String, MutableList<PossibleScheduling>>()

        aimaUnitRepository.getAimaUnitsByCity(city) { aimaUnits ->
            if (aimaUnits.isNotEmpty()) {
                val totalUnits = aimaUnits.size
                var processedUnits = 0

                for ((id, unit) in aimaUnits) {
                    getAvailableDayTime(id, unit, day) { timesList ->
                        availableTimeByUnit[id] = timesList
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

}
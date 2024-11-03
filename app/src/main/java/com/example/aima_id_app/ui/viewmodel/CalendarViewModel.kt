package com.example.aima_id_app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.AimaUnit
import com.example.aima_id_app.data.model.db_model.Appointment
import com.example.aima_id_app.data.repository.AimaUnitRepository
import com.example.aima_id_app.data.repository.AppointmentRepository
import com.example.aima_id_app.util.enums.PossibleScheduling
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel(

    private val TAG: String = "DEBUG",
    private val appointmentRepository: AppointmentRepository = AppointmentRepository(),
    private val aimaUnitRepository: AimaUnitRepository = AimaUnitRepository(),

): ViewModel() {

    init {
        loadAvailableCities()
    }

    private val _cities = MutableLiveData<List<String>>()
    val cities: MutableLiveData<List<String>> = _cities

    private fun loadAvailableCities(){
        aimaUnitRepository.findAllAimaUnits { units ->
            if (units.isNotEmpty()){
                _cities.value = units.values
                    .map { it.address.city }
                    .distinct()
            } else {
                Log.d(TAG, "No cities found")
            }
        }
    }

    fun getUnavailableDays(city: String, month: YearMonth, onComplete: (List<Int>) -> Unit) {
        Log.d(TAG, "getUnavailableDays called")

        val unavailableDays = mutableListOf<Int>()
        val daysInMonth = month.lengthOfMonth()
        var checksCompleted = 0

        for (day in 1..daysInMonth) {
            val date = month.atDay(day)
            isDayAvailable(city, date) { isAvailable ->
                Log.d(TAG, "checking day $date : $isAvailable")

                if (!isAvailable) {
                    unavailableDays.add(day)
                }
                checksCompleted++
                if (checksCompleted == daysInMonth) {
                    onComplete(unavailableDays)
                }
            }
        }
    }

    private fun isDayAvailable(city: String, day: LocalDate, callback: (Boolean) -> Unit) {
        Log.d(TAG, "IsDayAvailable called")

        getAimaUnitsByCity(city) { units ->
            if (units.isNotEmpty()) {
                var foundAvailableUnit = false
                var unitsChecked = 0
                val totalUnits = units.size

                for ((unitId, _) in units) {

                    Log.d(TAG, "loop $unitsChecked")

                    isUnitAvailable(unitId, day) { isAvailable ->
                        unitsChecked++

                        if (isAvailable) {

                            if (!foundAvailableUnit) {
                                foundAvailableUnit = true
                                callback(true)
                            }

                        } else if (unitsChecked == totalUnits) {
                            if (!foundAvailableUnit) {
                                callback(false)
                            }
                        }
                    }
                }
            } else {
                Log.d(TAG, "No Aima Units found for city $city")
                callback(false)
            }
        }
    }

    private fun isUnitAvailable(aimaUnitId: String, day: LocalDate, onComplete:
        (Boolean) -> Unit) {

        aimaUnitRepository.countStaff(aimaUnitId) { staffs ->
                val dailyPossibleScheduling = PossibleScheduling.entries.size
                //val unitDailyCapacity = staffs * dailyPossibleScheduling
                val unitDailyCapacity = dailyPossibleScheduling

                getAppointmentsByUnit(aimaUnitId, day) { unitAppointments ->
                    val dayAppointments = unitAppointments.size
                    if (dayAppointments < unitDailyCapacity){
                        onComplete(true)
                    } else {
                        onComplete (false)
                    }
                }
            }
    }

    private fun getAppointmentsByUnit(aimaUnitId: String, day: LocalDate, onComplete:
        (List<Appointment>) -> Unit) {
            appointmentRepository.filterAppointmentsByUnit(aimaUnitId) { unitAppointments ->
                if (unitAppointments.isNotEmpty()) {
                    val date = day.toString()
                    val filteredAppointments = unitAppointments.filter { it.date == date }
                    onComplete(filteredAppointments)
                } else {
                    Log.d(TAG, "No Appointment to $aimaUnitId were found")
                    onComplete(emptyList())
                }
            }
    }

    private fun getAimaUnitsByCity(city: String, onComplete: (Map<String, AimaUnit>) -> Unit) {
        aimaUnitRepository.getAimaUnitsByCity(city) { aimaUnits ->
            onComplete(aimaUnits)
        }
    }

}
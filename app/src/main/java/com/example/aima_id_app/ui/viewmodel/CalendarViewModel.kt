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

/**
 * ViewModel for managing the calendar-related functionalities of the application.
 *
 * This class handles the logic for loading available cities, checking the availability
 * of days for appointments, and determining the availability of specific Aima Units
 * on given days. It interacts with the `AppointmentRepository` and `AimaUnitRepository`
 * to fetch necessary data and to perform CRUD operations on appointments and Aima Units.
 *
 * The `CalendarViewModel` exposes a LiveData list of available cities and provides
 * methods to retrieve unavailable days, check day availability, and manage appointment
 * scheduling based on unit capacities.
 *
 * @property appointmentRepository The repository for managing appointments.
 * @property aimaUnitRepository The repository for managing Aima Units.
 * @constructor Creates an instance of CalendarViewModel with the provided repositories.
 */
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

    /**
     * Loads available cities from the AimaUnit repository and updates the LiveData.
     * Logs a message if no cities are found.
     */
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

    /**
     * Retrieves the unavailable days for scheduling in a specified city and month.
     *
     * @param city The city to check availability.
     * @param month The month to check for unavailable days.
     * @param onComplete Callback with the list of unavailable days.
     */
    fun getUnavailableDays(city: String, month: YearMonth, onComplete: (List<Int>) -> Unit) {
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

    /**
     * Checks if a specific day is available to scheduling
     * in a city by looking at its Aima Units.
     *
     * @param city The city to check.
     * @param day The specific day to check for availability.
     * @param callback Callback that returns the availability status.
     */
    private fun isDayAvailable(city: String, day: LocalDate, callback: (Boolean) -> Unit) {
        getAimaUnitsByCity(city) { units ->
            if (units.isNotEmpty()) {
                var foundAvailableUnit = false
                var unitsChecked = 0
                val totalUnits = units.size

                for ((unitId, _) in units) {

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

    /**
     * Checks if a specific Aima Unit is available for scheduling on a given day.
     *
     * @param aimaUnitId The ID of the Aima Unit to check.
     * @param day The specific day to check.
     * @param onComplete Callback with the availability status.
     */
    private fun isUnitAvailable(aimaUnitId: String, day: LocalDate, onComplete:
        (Boolean) -> Unit) {

        aimaUnitRepository.countStaff(aimaUnitId) { staffs ->
                val dailyPossibleScheduling = PossibleScheduling.entries.size
                val unitDailyCapacity = staffs * dailyPossibleScheduling

                getAppointmentsByUnit(aimaUnitId, day) { unitAppointments ->
                    val dayAppointments = unitAppointments.size
                    Log.d("DEBUG", "Unidade $aimaUnitId: agendamentos dia: ${day}  capacidade: $unitDailyCapacity")

                    if (dayAppointments < unitDailyCapacity){
                        onComplete(true)
                    } else {
                        onComplete (false)
                    }
                }
            }
    }

    /**
     * Retrieves all appointments for a specific Aima Unit on a given day.
     *
     * @param aimaUnitId The ID of the Aima Unit.
     * @param day The specific day to check.
     * @param onComplete Callback with the list of appointments.
     */
    private fun getAppointmentsByUnit(aimaUnitId: String, day: LocalDate, onComplete:
        (List<Appointment>) -> Unit) {
            appointmentRepository.filterAppointmentsByUnit(aimaUnitId) { unitAppointments ->
                if (unitAppointments.isNotEmpty()) {
                    val date = day.toString()
                    val filteredAppointments = unitAppointments.filter { it.date == date }
                    onComplete(filteredAppointments)
                } else {
                    onComplete(emptyList())
                }
            }
    }

    /**
     * Retrieves Aima Units by city from the repository.
     *
     * @param city The city to filter Aima Units.
     * @param onComplete Callback with the map of Aima Units.
     */
    private fun getAimaUnitsByCity(city: String, onComplete: (Map<String, AimaUnit>) -> Unit) {
        aimaUnitRepository.getAimaUnitsByCity(city) { aimaUnits ->
            onComplete(aimaUnits)
        }
    }

}
package com.example.aima_id_app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.AimaProcess
import com.example.aima_id_app.data.model.db_model.Appointment
import com.example.aima_id_app.data.repository.AimaProcessRepository
import com.example.aima_id_app.data.repository.AppointmentRepository
import com.example.aima_id_app.data.repository.ServiceRepository
import com.example.aima_id_app.util.enums.ProcessStatus
import com.google.firebase.auth.FirebaseAuth

/**
 * ViewModel class for managing the state and logic related to Aima process data and appointments.
 *
 * This ViewModel provides data and methods to retrieve and manage `AimaProcess` and `Appointment`
 * instances based on the authenticated user. It interacts with `AimaProcessRepository` and
 * `AppointmentRepository` to retrieve data from the data layer.
 *
 * @property aimaProcessRepository A repository for accessing and managing Aima processes.
 * @property auth Firebase Authentication instance for identifying the current user.
 * @property appointmentRepository A repository for accessing and managing appointments.
 */
class StatusViewModel(
    private val aimaProcessRepository: AimaProcessRepository = AimaProcessRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val appointmentRepository: AppointmentRepository = AppointmentRepository()
) : ViewModel() {

    // LiveData to store a map of AimaProcess instances, keyed by process ID.
    private val _processes = MutableLiveData<MutableMap<String, AimaProcess>>()
    val processes: LiveData<MutableMap<String, AimaProcess>> = _processes

    private val _services = MutableLiveData<MutableMap<String, String>>()
    val process_service: LiveData<MutableMap<String, String>> = _services

    private val _listaDePares = MutableLiveData<List<Pair<String, String>>>()
    val listaDePares: LiveData<List<Pair<String, String>>> = _listaDePares


    /**
     * Fetches all Aima processes associated with the currently authenticated user.
     *
     * This function retrieves processes for the current user and updates `_processes`
     * with a map of AimaProcess instances, keyed by their ID. If the user is not authenticated,
     * the function returns immediately without updating data.
     */
    fun getProcessesByUserId(onComplete: (List<Pair<String, String>>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        ServiceRepository().getAll { services ->
            Log.d("PROCESS", "${services.size}")
            aimaProcessRepository.filterProcessesByUser(userId) { aimaProcesses ->
                Log.d("PROCESS", "${aimaProcesses.size}")

                _processes.value = aimaProcesses

                var process_service = mutableMapOf<String, String>()

                for (process in aimaProcesses){
                    val service = services.filter { it.code == process.value.serviceCode }[0]
                    process_service[process.key] = service.name
                }
                onComplete(process_service.toList())


                val pairList = process_service.toList()
                _services.value = process_service
            }

        }

    }

    /**
     * Retrieves an appointment associated with a specific Aima process ID.
     *
     * This function uses the provided process ID to fetch the corresponding appointment
     * and invokes the `onComplete` callback with the retrieved `Appointment` instance.
     * If no appointment is found, `onComplete` is called with `null`.
     *
     * @param id The ID of the Aima process whose appointment needs to be fetched.
     * @param onComplete A callback function that receives the retrieved `Appointment` or null if not found.
     */
    fun getAppointmentByProcess(id: String, onComplete: (Appointment?) -> Unit) {

        appointmentRepository.getAppointmentByProcess(id) { appointment ->

            Log.d("PROCESS", "${appointment?.processId}")
            onComplete(appointment)
        }
    }
}

package com.example.aima_id_app.data.repository

import android.util.Log
import com.example.aima_id_app.data.model.db_model.Appointment
import com.example.aima_id_app.util.enums.PossibleScheduling
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.firestore.toObject

/**
 * Repository class for managing `Appointment` data with Firebase Firestore.
 * Provides methods for creating, reading, updating, and deleting `Appointment`
 * records, as well as filtering and checking records based on specific criteria.
 */
class AppointmentRepository {

    private val db = FirebaseFirestore.getInstance().collection("appointments")


    /**
     * Adds a new `Appointment` record to Firestore.
     *
     * @param appointment The `Appointment` object to be added.
     * @param onComplete Callback indicating success (`true`) or failure (`false`).
     */
    fun createAppointment(appointment: Appointment, onComplete: (Boolean) -> Unit) {
        db.add(appointment)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Retrieves an `Appointment` by its unique ID.
     *
     * @param id The unique identifier of the `Appointment`.
     * @param onComplete Callback returning the `Appointment` if found, or `null` if not.
     */
    fun findAppointmentById(id: String, onComplete: (Appointment?) -> Unit) {
        db.document(id).get()
            .addOnSuccessListener { document ->
                onComplete(document.toObject(Appointment::class.java))
            }
            .addOnFailureListener { onComplete(null) }
    }

    /**
     * Retrieves all `Appointment` records associated with a specific user ID.
     *
     * @param userId The ID of the user whose appointments are to be retrieved.
     * @param onComplete Callback returning a list of `Appointment` objects, empty if none found.
     */
    fun filterAppointmentsByUserId(userId: String, onComplete: (MutableList<Appointment>) -> Unit) {
        db.whereEqualTo("userId", userId).get()
            .addOnSuccessListener { userAppointments ->
                val appointments = mutableListOf<Appointment>()
                for (document in userAppointments) {
                    appointments.add(document.toObject(Appointment::class.java))
                }
                onComplete(appointments)
            }
            .addOnFailureListener { onComplete(mutableListOf()) }
    }

    /**
     * Retrieves all `Appointment` records associated with a specific aimaUnit ID.
     *
     * @param aimaUnitId The aimaUnit ID to filter `Appointment` objects by.
     * @param onComplete Callback returning a list of `Appointment` objects, empty if none found.
     */
    fun filterAppointmentsByUnit(aimaUnitId: String, onComplete: (MutableList<Appointment>) -> Unit) {
        db.whereEqualTo("aimaUnitId", aimaUnitId).get()
            .addOnSuccessListener { unitAppointments ->
                val appointments = mutableListOf<Appointment>()
                for (document in unitAppointments) {
                    appointments.add(document.toObject(Appointment::class.java))
                }
                onComplete(appointments)
            }
            .addOnFailureListener { onComplete(mutableListOf()) }
    }

    /**
     * Retrieves all `Appointment` records for a specified date.
     *
     * @param date The date to filter `Appointment` objects by.
     * @param onComplete Callback returning a list of `Appointment` objects, empty if none found.
     */
    fun filterAppointmentsByDate(date: String, onComplete: (MutableList<Appointment>) -> Unit) {
        db.whereEqualTo("date", date).get()
            .addOnSuccessListener { dayAppointments ->
                val appointments = mutableListOf<Appointment>()
                for (document in dayAppointments) {
                    appointments.add(document.toObject(Appointment::class.java))
                }
                onComplete(appointments)
            }
            .addOnFailureListener { onComplete(mutableListOf()) }
    }

    /**
     * Checks if any `Appointment` exists for a given process ID.
     *
     * @param processId The ID of the process to check for associated appointments.
     * @param onComplete Callback returning `true` if appointments exist, `false` otherwise.
     */
    fun checkAppointmentByProcess(processId: String, onComplete: (Boolean) -> Unit) {
        db.whereEqualTo("processId", processId).get()
            .addOnSuccessListener { querySnapshot ->
                onComplete(querySnapshot.documents.isNotEmpty())
            }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Retrieves an `Appointment` associated with a specific process ID.
     *
     * @param processId The process ID to retrieve the associated `Appointment`.
     * @param onComplete Callback returning the `Appointment` if found, or `null` if not.
     */
    fun getAppointmentByProcess(processId: String, onComplete: (Appointment?) -> Unit) {
        db.whereEqualTo("processId", processId).get()
            .addOnSuccessListener { appointment ->
                onComplete(appointment.documents.firstOrNull()?.toObject(Appointment::class.java))
            }
            .addOnFailureListener { onComplete(null) }
    }

    /**
     * Deletes an `Appointment` from Firestore by its unique ID.
     *
     * @param id The ID of the `Appointment` to be deleted.
     * @param onComplete Callback indicating success (`true`) or failure (`false`).
     */
    fun deleteAppointment(id: String, onComplete: (Boolean) -> Unit) {
        db.document(id).delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Deletes all `Appointment` records associated with a specified process ID.
     *
     * @param processId The process ID to filter `Appointment` records by for deletion.
     * @param onComplete Callback indicating success (`true`) or failure (`false`).
     */
    fun deleteAppointmentsByProcessId(processId: String, onComplete: (Boolean) -> Unit) {
        db.whereEqualTo("processId", processId).get()
            .addOnSuccessListener { processAppointments ->
                if (!processAppointments.isEmpty) {
                    val deleteTasks = mutableListOf<Task<Void>>()

                    for (appointment in processAppointments.documents) {
                        val task = db.document(appointment.id).delete()
                        deleteTasks.add(task)
                    }

                    Tasks.whenAllComplete(deleteTasks)
                        .addOnSuccessListener { onComplete(true) }
                        .addOnFailureListener { onComplete(false) }
                } else {
                    onComplete(true)
                }
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    /**
     * Updates an existing `Appointment` in the database.
     *
     * @param id The unique ID of the `Appointment` to be updated.
     * @param appointment The updated `Appointment` object.
     * @param onComplete A callback function that takes a Boolean indicating
     *                   whether the update was successful or not.
     */
    fun updateAppointment(id: String, appointment: Appointment, onComplete: (Boolean) -> Unit) {
        db.document(id).set(appointment)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Retrieves all `Appointment` records from Firestore.
     *
     * @param onComplete Callback returning a list of all `Appointment` objects, empty if none found.
     */
    fun findAllAppointments(onComplete: (MutableList<Appointment>) -> Unit) {
        db.get()
            .addOnSuccessListener { appointmentList ->
                val appointments = appointmentList.toObjects(Appointment::class.java).toMutableList()
                onComplete(appointments)
            }
            .addOnFailureListener { onComplete(mutableListOf()) }
    }


    fun findDayAppointmentOfUnit(aimaUnitId: String, day: String,
                                 onComplete: (MutableList<Appointment>) -> Unit){
        db.whereEqualTo("aimaUnitId", aimaUnitId).whereEqualTo("date", day).get()
            .addOnSuccessListener { appointments ->

                Log.d("DEBUG", "Day appointments to unit $aimaUnitId:  ${appointments.size()}")
                val appointmentList = mutableListOf<Appointment>()
                for (appointment in appointments){
                    val ap = appointment.toObject<Appointment>()
                    Log.d("DEBUG", "    Appointment time: ${ap.time}")
                    appointmentList.add(ap)
                }
                onComplete(appointmentList)
            }
            .addOnFailureListener{ exception ->
                Log.d("DEBUG", exception.toString())
            }
    }

}
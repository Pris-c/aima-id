package com.example.aima_id_app.data.repository

import android.util.Log
import com.example.aima_id_app.data.model.db_model.Appointment
import com.example.aima_id_app.util.enums.PossibleScheduling
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class AppointmentRepository() {

    private val db = FirebaseFirestore.getInstance().collection("appointments")


    /**
     * Creates a new `Appointment` in the Firestore database.
     *
     * @param appointment The `Appointment` object to be created.
     * @param onComplete A callback function that takes a Boolean indicating
     *                   the success of the operation.
     */
    fun createAppointment(appointment: Appointment, onComplete: (Boolean) -> Unit) {
        db.add(appointment)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Finds an `Appointment` by its unique identifier (ID).
     *
     * @param id The unique ID of the `Appointment` to be retrieved.
     * @param onComplete A callback function that takes an optional `Appointment`
     *                   object. If the appointment is found, it returns the appointment;
     *                   otherwise, it returns null.
     */
    fun findAppointmentById(id: String, onComplete: (Appointment?) -> Unit) {
        db.document(id).get()
            .addOnSuccessListener { document ->
                onComplete(document.toObject(Appointment::class.java))
            }
            .addOnFailureListener { onComplete(null) }
    }

    /**
     * Filter `Appointment` objects by user ID.
     *
     * @param userId The user ID to filter `Appointment` objects.
     * @param onComplete A callback function that takes a mutable list of
     *                   `Appointment` objects found for the specified user ID.
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
     * Filter `Appointment` objects by aimaUnit ID.
     *
     * @param aimaUnitId The aimaUnit ID to filter `Appointment` objects.
     * @param onComplete A callback function that takes a mutable list of
     *                   `Appointment` objects found for the specified aimaUnit ID.
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
     * Filter `Appointment` objects by date.
     *
     * @param date The date to filter `Appointment` objects.
     * @param onComplete A callback function that takes a mutable list of
     *                   `Appointment` objects found for the specified date.
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
     * Checks if any appointments exist for a given process ID.
     *
     * @param processId The ID of the process to check for appointments.
     * @param onComplete Callback that returns `true` if appointments exist, or `false` if not
     * or if an error occurs.
     *
     */
    fun checkAppointmentByProcess(processId: String, onComplete: (Boolean) -> Unit) {
        db.whereEqualTo("processId", processId).get()
            .addOnSuccessListener { querySnapshot ->
                onComplete(querySnapshot.documents.isNotEmpty())
            }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Deletes an `Appointment` from the database.
     *
     * @param id The unique ID of the `Appointment` to be deleted.
     * @param onComplete A callback function that takes a Boolean indicating
     *                   whether the deletion was successful or not.
     */
    fun deleteAppointment(id: String, onComplete: (Boolean) -> Unit) {
        db.document(id).delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Deletes all `Appointment` objects that match the specified process ID.
     *
     * @param processId The process ID to filter `Appointment` objects.
     * @param onComplete A callback function that takes a Boolean indicating
     *                   whether the deletion was successful or not.
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
     * Retrieves all `Appointment` objects from Firestore.
     *
     * @param onComplete Callback receiving a mutable list of `Appointment` objects.
     */
    fun findAllAppointments(onComplete: (MutableList<Appointment>) -> Unit) {
        db.get()
            .addOnSuccessListener { appointmentList ->
                val appointments = mutableListOf<Appointment>()
                for (document in appointmentList) {
                    appointments.add(document.toObject(Appointment::class.java))
                }
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
package com.example.aima_id_app.data.repository

import com.example.aima_id_app.data.model.db_model.AimaProcess
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

/**
 * Repository for managing AimaProcess objects within a Firestore database.
 *
 * Provides methods to create, retrieve, update, and filter processes based on
 * criteria such as user ID and service code.
 */
class AimaProcessRepository {

    private val db = FirebaseFirestore.getInstance().collection("processes")

    /**
     * Adds a new process to the Firestore database.
     *
     * @param process The AimaProcess object to be added.
     * @param onComplete Callback invoked with `true` if the operation succeeds, `false` otherwise.
     */
    fun createProcess(process: AimaProcess, onComplete: (Boolean) -> Unit) {
        db.add(process)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Retrieves a specific process by its unique ID.
     *
     * @param id The ID of the process to retrieve.
     * @param onComplete Callback invoked with the retrieved AimaProcess object, or `null` if not found.
     */
    fun findProcessById(id: String, onComplete: (AimaProcess?) -> Unit) {
        db.document(id).get()
            .addOnSuccessListener { document -> onComplete(document.toObject<AimaProcess>()) }
            .addOnFailureListener { onComplete(null) }
    }

    /**
     * Retrieves processes filtered by service code.
     *
     * @param serviceCode The service code to match against.
     * @param onComplete Callback invoked with a list of processes matching the specified service code.
     */
    fun findProcessByType(serviceCode: String, onComplete: (MutableList<AimaProcess>) -> Unit) {
        db.whereEqualTo("serviceCode", serviceCode).get()
            .addOnSuccessListener { documents ->
                val processes = documents.mapNotNull { it.toObject<AimaProcess>() }.toMutableList()
                onComplete(processes)
            }
            .addOnFailureListener { onComplete(mutableListOf()) }
    }

    /**
     * Retrieves processes associated with a specific user ID.
     *
     * @param userId The user ID to match.
     * @param onComplete Callback invoked with a map of process IDs to AimaProcess objects.
     */
    fun filterProcessesByUser(userId: String, onComplete: (MutableMap<String, AimaProcess>) -> Unit) {
        val userProcesses = mutableMapOf<String, AimaProcess>()

        db.whereEqualTo("userId", userId).get()
            .addOnSuccessListener { documents ->
                documents.forEach { document ->
                    userProcesses[document.id] = document.toObject<AimaProcess>()
                }
                onComplete(userProcesses)
            }
            .addOnFailureListener { onComplete(userProcesses) }
    }

    /**
     * Retrieves processes associated with a specific user ID as a list.
     *
     * @param userId The user ID to match.
     * @param onComplete Callback invoked with a list of processes associated with the specified user ID.
     */
    fun findProcessesByUser(userId: String, onComplete: (MutableList<AimaProcess>) -> Unit) {
        db.whereEqualTo("userId", userId).get()
            .addOnSuccessListener { documents ->
                val processes = documents.mapNotNull { it.toObject<AimaProcess>() }.toMutableList()
                onComplete(processes)
            }
            .addOnFailureListener { onComplete(mutableListOf()) }
    }

    /**
     * Retrieves all processes from the Firestore database.
     *
     * @param onComplete Callback invoked with a list of all processes, or an empty list if none are found.
     */
    fun findAllProcesses(onComplete: (MutableList<AimaProcess>) -> Unit) {
        db.get()
            .addOnSuccessListener { documents ->
                val processes = documents.mapNotNull { it.toObject<AimaProcess>() }.toMutableList()
                onComplete(processes)
            }
            .addOnFailureListener { onComplete(mutableListOf()) }
    }

    /**
     * Updates an existing process in the Firestore database.
     *
     * @param id The ID of the process to update.
     * @param process The updated AimaProcess object.
     * @param onComplete Callback invoked with `true` if the update succeeds, `false` otherwise.
     */
    fun updateProcess(id: String, process: AimaProcess, onComplete: (Boolean) -> Unit) {
        db.document(id).set(process)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}

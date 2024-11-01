package com.example.aima_id_app.data.repository

import com.example.aima_id_app.data.model.db_model.AimaProcess
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

/**
 * A repository class for managing processes in a Firestore database.
 *
 * This class provides methods to create, retrieve, update, and find processes
 * associated with different criteria such as type and user ID.
 */
class AimaProcessRepository {

    private val db = FirebaseFirestore.getInstance().collection("processes")

    /**
     * Creates a new process in the Firestore database.
     *
     * @param process The process object to be added to the database.
     * @param onComplete A callback function that is invoked with a Boolean value
     *                   indicating success (true) or failure (false) of the operation.
     */
    fun createProcess(process: AimaProcess, onComplete: (Boolean) -> Unit){
        db.add(process)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    /**
     * Retrieves a process by its ID from the Firestore database.
     *
     * @param id The unique identifier of the process to retrieve.
     * @param onComplete A callback function that is invoked with the retrieved process
     *                   object or null if the process is not found.
     */
    fun findProcessById(id: String,  onComplete: (AimaProcess?) -> Unit) {
        db.document(id).get()
            .addOnSuccessListener { document ->
                onComplete(document.toObject<AimaProcess>())
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    /**
     * Finds processes by their service code.
     *
     * @param serviceCode The service code to filter processes.
     * @param onComplete A callback function that is invoked with a mutable list of
     *                   processes that match the service code.
     */
    fun findProcessByType(serviceCode: String, onComplete: (MutableList<AimaProcess>) -> Unit) {
        db.whereEqualTo("serviceCode", serviceCode).get()
            .addOnSuccessListener { list ->
                val processes = mutableListOf<AimaProcess>()
                for (document in list){
                    val process = document.toObject<AimaProcess>()
                    processes.add(process)
                }
                onComplete(processes)
            }
            .addOnFailureListener{
                onComplete(mutableListOf())
            }
    }

    /**
     * Finds processes associated with a specific user ID.
     *
     * @param userId The user ID to filter processes.
     * @param onComplete A callback function that is invoked with a mutable list of
     *                   processes associated with the user ID.
     */
    fun findProcessesByUser(userId: String, onComplete: (MutableList<AimaProcess>) -> Unit) {
        db.whereEqualTo("userId", userId).get()
            .addOnSuccessListener { list ->
                val processes = mutableListOf<AimaProcess>()
                for (document in list){
                    val process = document.toObject<AimaProcess>()
                    processes.add(process)
                }
                onComplete(processes)
            }
            .addOnFailureListener{
                onComplete(mutableListOf())
            }
    }

    /**
     * Retrieves all processes from the Firestore database.
     *
     * @param onComplete A callback function that is invoked with a mutable list of
     *                   all processes retrieved from the database.
     */
    fun findAllProcesses(onComplete: (MutableList<AimaProcess>) -> Unit) {
        db.get()
            .addOnSuccessListener { list ->
                val processes = mutableListOf<AimaProcess>()
                for (document in list) {
                    val process = document.toObject<AimaProcess>()
                    processes.add(process)
                }
                onComplete(processes)
            }
            .addOnFailureListener {
                onComplete(mutableListOf())
            }
    }

    /**
     * Updates an existing process in the Firestore database.
     *
     * @param id The unique identifier of the process to update.
     * @param process The updated process object.
     * @param onComplete A callback function that is invoked with a Boolean value
     *                   indicating success (true) or failure (false) of the operation.
     */
    fun updateProcess(id: String, process: AimaProcess, onComplete: (Boolean?) -> Unit) {
        db.document(id).set(process)
            .addOnSuccessListener {
                onComplete(true)
            }

            .addOnFailureListener {
                onComplete(false)
            }
    }

}
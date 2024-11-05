package com.example.aima_id_app.data.repository

import com.example.aima_id_app.data.model.db_model.Service
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

/**
 * Repository for managing service-related data operations in Firebase Firestore.
 * This class provides methods for creating, retrieving, and fetching all services
 * from the Firestore "services" collection.
 */
class ServiceRepository {

    private val db = FirebaseFirestore.getInstance().collection("services")

    /**
     * Creates or updates a service document in the Firestore database.
     *
     * @param id The unique ID for the service document to be created or updated.
     * @param service The Service object containing the data to be stored.
     * @param onComplete Callback function that is invoked after the operation.
     *                   It returns true if the operation succeeds, or false if it fails.
     */
    fun createService(id: String, service: Service, onComplete: (Boolean?) -> Unit) {
        db.document(id).set(service)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    /**
     * Retrieves a service document by its ID from the Firestore database.
     *
     * @param id The unique ID of the service to retrieve.
     * @param onComplete Callback function that receives the retrieved Service object if successful,
     *                   or null if the document is not found or the operation fails.
     */
    fun findServiceById(id: String, onComplete: (Service?) -> Unit) {
        db.document(id).get()
            .addOnSuccessListener { document ->
                onComplete(document.toObject<Service>())
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    /**
     * Retrieves all service documents from the Firestore database.
     *
     * @param onComplete Callback function that returns a MutableList of Service objects.
     *                   If the operation fails, the list will be empty.
     */
    fun getAll(onComplete: (MutableList<Service>) -> Unit) {
        val services: MutableList<Service> = mutableListOf()

        db.get()
            .addOnSuccessListener { list ->
                for (document in list) {
                    val service = document.toObject<Service>()
                    if (service != null) {
                        services.add(service)
                    }
                }
                onComplete(services)
            }
            .addOnFailureListener {
                onComplete(services)
            }
    }
}

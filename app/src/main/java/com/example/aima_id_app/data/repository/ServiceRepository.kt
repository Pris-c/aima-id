package com.example.aima_id_app.data.repository

import com.example.aima_id_app.data.model.db_model.AdminUser
import com.example.aima_id_app.data.model.db_model.Service
import com.example.aima_id_app.data.model.db_model.ServiceUser
import com.example.aima_id_app.data.model.db_model.StaffUser
import com.example.aima_id_app.data.model.db_model.User
import com.example.aima_id_app.util.enums.UserRole
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class ServiceRepository {

    private val db = FirebaseFirestore.getInstance().collection("services")


    /**
     * Creates a new service in the database with the given ID.
     *
     * @param id The ID of the service to be created.
     * @param service The service object to be stored.
     * @param onComplete A callback function that returns true if the operation is successful,
     * false otherwise.
     */
    fun createService(id: String, service: Service,  onComplete: (Boolean?) -> Unit) {
        db.document(id).set(service)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }


    /**
     * Finds a service by its ID in the database.
     *
     * @param id The ID of the service.
     * @param onComplete A callback function that returns the service if found, or null if the
     * operation fails.
     */
    fun findServiceById(id: String,  onComplete: (Service?) -> Unit) {
        db.document(id).get()
            .addOnSuccessListener { document ->
                onComplete(document.toObject<Service>())
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    /**
     * Finds all service in the database.
     *
     * @param onComplete A callback function that returns a MutableList of services
     */
    fun getAll(onComplete: (MutableList<Service>) -> Unit){
        val services: MutableList<Service> = mutableListOf<Service>()

        db.get()
            .addOnSuccessListener { list ->
                for (document in list){
                    val service = document.toObject<Service>()
                    services.add(service)
                }
                onComplete(services)
            }
            .addOnFailureListener{
                onComplete(services)
            }
    }

}
package com.example.aima_id_app.data.repository

import androidx.lifecycle.get
import com.example.aima_id_app.data.model.db_model.AdminUser
import com.example.aima_id_app.data.model.db_model.ServiceUser
import com.example.aima_id_app.data.model.db_model.StaffUser
import com.example.aima_id_app.data.model.db_model.User
import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.util.enums.UserRole
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

/**
 * Repository class for managing user-related operations in the Firestore database.
 *
 * Provides methods for creating, finding, updating, and deleting users.
 */
class UserRepository {

    private val db = FirebaseFirestore.getInstance().collection("users")
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Creates a new user in the database with the given ID.
     *
     * @param id The ID of the user to be created.
     * @param user The user object to be stored.
     * @param onComplete A callback function that returns true if the operation is successful, false otherwise.
     */
    fun createUser(id: String, user: User,  onComplete: (Boolean?) -> Unit) {
        db.document(id).set(user)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    /**
     * Finds a user by their ID in the database.
     *
     * @param id The ID of the user to be retrieved.
     * @param onComplete A callback function that returns the user if found, or null if the operation fails.
     */
    fun findUserById(id: String,  onComplete: (User?) -> Unit) {
        db.document(id).get()
            .addOnSuccessListener { document ->
                val role = document.getString("role")
                var user: User?=null
                when (role) {

                    UserRole.SERVICE_USER.role -> user = document.toObject<ServiceUser>()
                    UserRole.ADMIN.role -> user = document.toObject<AdminUser>()
                    UserRole.STAFF.role -> user = document.toObject<StaffUser>()
                }
                onComplete(user)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    /**
     * Finds a user by their NIF in the database.
     *
     * @param nif The NIF of the user to be retrieved.
     * @param onComplete A callback function that returns the user if found, or null if the operation fails.
     */
    fun findUserByNif(nif: String, onComplete: (User?) -> Unit) {
        db.whereEqualTo("nif", nif).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty){
                    val document = documents.documents.first()
                    val role = document.getString("role")
                    var user: User?=null
                    when (role) {
                        UserRole.SERVICE_USER.role -> user = document.toObject<ServiceUser>()
                        UserRole.ADMIN.role -> user = document.toObject<AdminUser>()
                        UserRole.STAFF.role -> user = document.toObject<StaffUser>()
                    }
                    onComplete(user)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    /**
     * Deletes a user from the database by their ID.
     *
     * @param id The ID of the user to be deleted.
     * @param onComplete A callback function that returns true if the operation is successful, false otherwise.
     */
    fun deleteUser(id: String,  onComplete: (Boolean?) -> Unit) {
        db.document(id).delete()
            .addOnSuccessListener {

                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }

    }

    /**
     * Updates an existing user in the database with the given ID.
     *
     * @param id The ID of the user to be updated.
     * @param user The updated user object.
     * @param onComplete A callback function that returns true if the operation is successful, false otherwise.
     */
    fun updateUser(id: String,user: User, onComplete: (Boolean?) -> Unit) {
        db.document(id).set(user)
            .addOnSuccessListener {
                onComplete(true)
            }

            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun getUserDocuments(uid: String, callback: (List<UserDocument>) -> Unit) {
        val userDocumentsRef = firestore.collection("userDocuments").whereEqualTo("userId", uid)

        userDocumentsRef.get()
            .addOnSuccessListener { documents ->
                val userDocuments = documents.mapNotNull { document ->
                    try {
                        document.toObject(UserDocument::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
                callback(userDocuments)
            }
            .addOnFailureListener { exception ->
                //
            }
    }
}


package com.example.aima_id_app.data.repository

import com.example.aima_id_app.data.model.db_model.AdminUser
import com.example.aima_id_app.data.model.db_model.ServiceUser
import com.example.aima_id_app.data.model.db_model.StaffUser
import com.example.aima_id_app.data.model.db_model.User
import com.example.aima_id_app.util.enums.UserRole
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = FirebaseFirestore.getInstance().collection("users")

    fun createUser(id: String, user: User,  onComplete: (Boolean?) -> Unit) {
        db.document(id).set(user)
            .addOnSuccessListener {

                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

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

    fun findUserByNif(nif: String, onComplete: (User?) -> Unit) {
        db.document(nif).get()
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

    fun deleteUser(id: String,  onComplete: (Boolean?) -> Unit) {
        db.document(id).delete()
            .addOnSuccessListener {

                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }

    }


    fun updateUser(id: String,user: User, onComplete: (Boolean?) -> Unit) {
        db.document(id).set(user)
            .addOnSuccessListener {
                onComplete(true)
            }

            .addOnFailureListener {
                onComplete(false)
            }
    }
}


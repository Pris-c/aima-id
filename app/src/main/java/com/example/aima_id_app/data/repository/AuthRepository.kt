package com.example.aima_id_app.data.repository

import com.example.aima_id_app.data.service.UserRegistrationService.UserRegistrationCallBack
import com.google.firebase.auth.FirebaseAuth

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    public fun register(email: String, password: String, callback: UserRegistrationCallBack) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: " "
                callback.onSuccess(userId.toString())
            }
            .addOnFailureListener {
                callback.onFailure(null.toString())
            }
    }

    fun register2(email: String, password: String, onComplete: (String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    val userId = result.result?.user?.uid
                    onComplete(userId)
                } else {
                    onComplete(null)
                }
            }
    }

}

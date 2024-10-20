package com.example.aima_id_app.data.repository

import android.R
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun register(email: String, password: String, onComplete: (String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid
                    sendEmailVerification(userId.toString())
                    onComplete(userId)
                } else {
                    onComplete(null)
                }
            }
    }

    private fun sendEmailVerification(userId: String) {
        val auth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth.currentUser
        if (user != null && user.uid == userId) {
            user.sendEmailVerification()
        }
    }


}

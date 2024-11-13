package com.example.aima_id_app.data.repository


import android.util.Log
import com.example.aima_id_app.data.model.db_model.User
import com.example.aima_id_app.util.enums.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

/**
 * AuthRepository is responsible for managing user authentication operations
 * with Firebase Authentication, including user registration and login.
 */
class AuthRepository(
    val userRepository: UserRepository = UserRepository()
) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Registers a new user with the specified email and password.
     *
     * @param email The email address of the user to be registered.
     * @param password The password of the user to be registered.
     * @param onComplete A callback that returns a String? containing the
     *                   user ID if registration was successful, or null if it failed.
     */
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

    /**
     * Logs in a user with the specified email and password.
     *
     * @param email The email address of the user trying to log in.
     * @param password The password of the user trying to log in.
     * @param onComplete A callback that returns the user ID as a String if the login was successful,
     *                   or null if the login failed.
     */
    fun login(email: String, password: String, onComplete: (User?) -> Unit){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        userRepository.findUserById(userId.toString()) { user ->
                            if (user != null) {
                                val userRole = UserRole.fromRole(user.role)
                                onComplete(user)
                            }
                        }
                    } else {
                        onComplete(null)
                    }
                } else {
                    onComplete(null)
                }
            }
    }

    /**
     * Sends an email verification to the user with the specified user ID.
     *
     * @param userId The ID of the user to whom the verification email will be sent.
     */
    private fun sendEmailVerification(userId: String) {
        val auth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth.currentUser
        if (user != null && user.uid == userId) {
            user.sendEmailVerification()
        }
    }


}

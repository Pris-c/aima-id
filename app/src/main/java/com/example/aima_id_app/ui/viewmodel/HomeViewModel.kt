package com.example.aima_id_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.User
import com.example.aima_id_app.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth

class HomeViewModel: ViewModel() {

    val userRepository = UserRepository()
    val auth = FirebaseAuth.getInstance()

    /**
     * Retrieves a user by their ID.
     *
     * This function attempts to retrieve a user from the repository using the provided `userId`.
     * If the user is found, the `onComplete` callback is invoked with the User object.
     * If the user is not found or the `userId` is null, the `onComplete` callback is invoked with `null`.
     *
     * @param userId The ID of the user to retrieve.
     * @param onComplete A callback function that is invoked with the retrieved User object or `null`.
     */
    fun getUserById(userId: String, onComplete: (User?) -> Unit){
        if (userId != null){
            userRepository.findUserById(userId){ user ->
                if (user != null) {
                    onComplete(user)
                }
            }
        } else {
            onComplete(null)
        }
    }
}
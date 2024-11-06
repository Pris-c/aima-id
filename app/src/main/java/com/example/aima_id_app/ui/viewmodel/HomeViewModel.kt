package com.example.aima_id_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.User
import com.example.aima_id_app.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth

class HomeViewModel: ViewModel() {

    val userRepository = UserRepository()
    val auth = FirebaseAuth.getInstance()

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
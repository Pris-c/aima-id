package com.example.aima_id_app.ui.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.repository.AuthRepository

/**
 * ViewModel for handling the login functionality in the application.
 *
 * This ViewModel interacts with the AuthRepository to manage user authentication,
 * and exposes LiveData objects to observe the login status and navigation events.
 */
class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _loginError = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _loginError

    private val _navigateToActivity = MutableLiveData<Class<*>>()
    val navigateToActivity: LiveData<Class<*>> get() = _navigateToActivity


    /**
     * Attempts to log in the user with the provided email and password.
     *
     * @param email The email address of the user.
     * @param password The password for the user.
     *
     * If the login is successful, it updates the navigation LiveData to direct the user
     * to the UserActivity. If the login fails, it sets an error message in the LiveData.
     */
    fun login(email: String, password: String) {
        authRepository.login(email, password) { userId ->
            if (userId != null){
               _navigateToActivity.value = UserActivity::class.java
            } else {
                _loginError.value = "Login falhou. Tente novamente."
            }
        }
    }
}

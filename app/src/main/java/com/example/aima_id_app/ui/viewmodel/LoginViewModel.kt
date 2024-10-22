package com.example.aima_id_app.ui.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.repository.AuthRepository

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _loginError = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _loginError

    private val _navigateToActivity = MutableLiveData<Class<*>>()
    val navigateToActivity: LiveData<Class<*>> get() = _navigateToActivity

//---------------------------------------------------------------------------------

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

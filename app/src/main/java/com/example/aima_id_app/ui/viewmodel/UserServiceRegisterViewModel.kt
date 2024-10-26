package com.example.aima_id_app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.ServiceUser
import com.example.aima_id_app.data.model.db_model.User
import com.example.aima_id_app.data.model.submodel.Address
import com.example.aima_id_app.data.repository.AuthRepository
import com.example.aima_id_app.data.repository.UserRepository
import com.example.aima_id_app.util.enums.UserRole
import com.example.aima_id_app.util.validators.AddressValidator
import com.example.aima_id_app.util.validators.UserValidator
import kotlinx.coroutines.scheduling.DefaultIoScheduler.default
import java.time.LocalDate

class UserServiceRegisterViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    //private val govApiService = GovApiService()
    private val userValidator = UserValidator()
    private val addressValidator = AddressValidator()
    private val userRepository = UserRepository()

    private val _registrationError = MutableLiveData<String?>()
    val registrationError: LiveData<String?> = _registrationError

    private val _navigateToActivity = MutableLiveData<Class<*>>()
    val navigateToActivity: LiveData<Class<*>> get() = _navigateToActivity

    fun register(
        name: String,
        email: String,
        nif: String,
        dateOfBirth: LocalDate,
        street: String,
        number: Int,
        city: String,
        postalCode: String,
        phone: String
    ) {
        if (!userValidator.isValidName(name) || !userValidator.isValidEmail(email) || !userValidator.isValidNIF(nif) || !userValidator.isValidDateOfBirth(dateOfBirth)) {
            _registrationError.value = "Invalid user data."
            return
        }
        if (!addressValidator.isValidCity(city) || !addressValidator.isValidNumber(number) || !addressValidator.isValidStreet(street) || !addressValidator.isValidPostalCode(postalCode)){
            _registrationError.value = "Invalid address data."
        }


        //govApiService.userValidator (name, nif, dateOfBirth) { success ->
          //  if (!govValidation) {
            //    _registrationError.value = "Failed government validation."
              //  return@validateUser
            //}

            authRepository.register(email, password) { userId ->
                if (userId == null) {
                    _registrationError.value = "Failed to register user in authentication service."
                }

                val address = Address(
                    street = street,
                    number = number,
                    city = city,
                    postalCode = postalCode
                )

                val serviceUser = ServiceUser(
                    email = email,
                    nif = nif,
                    name = name,
                    dateOfBirth = dateOfBirth,
                    phone = phone,
                    address = address
                )

        userRepository.createUser { success ->

                    if (success) {
                        _navigateToActivity.value = UserActivity::class.java
                    } else {
                        _registrationError.value = "Failed to save user data."
                    }
                }
            }
        }
    }
}


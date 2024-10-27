package com.example.aima_id_app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.ServiceUser
import com.example.aima_id_app.data.model.submodel.Address
import com.example.aima_id_app.data.repository.AuthRepository
import com.example.aima_id_app.data.repository.UserRepository
import com.example.aima_id_app.util.validators.AddressValidator
import com.example.aima_id_app.util.validators.UserValidator
import java.time.LocalDate

/**
 * ViewModel responsible for managing the registration of a new ServiceUser in the system.
 * It handles the validation of user and address data, user authentication, and data storage.
 *
 * This ViewModel relies on multiple components to perform its tasks:
 * - AuthRepository: Manages authentication-related operations such as registering a user with email and password.
 * - UserValidator & AddressValidator: Validate user information (name, email, NIF, date of birth, phone) and address data (street, number, city, postal code) respectively.
 * - UserRepository: Handles storage of user data in the database.
 *
 * The ViewModel exposes LiveData properties to report errors during registration and navigate to a new activity if registration is successful.
 */
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

    /**
     * Função de registro que recebe dados do novo usuário e tenta registrá-lo no sistema.
     * Valida os dados, cria um objeto ServiceUser e armazena no banco de dados.
     *
     * @param name Nome do usuário a ser registrado.
     * @param email Endereço de e-mail do usuário.
     * @param nif Número de Identificação Fiscal do usuário.
     * @param dateOfBirth Data de nascimento do usuário.
     * @param street Rua do endereço do usuário.
     * @param number Número do endereço do usuário.
     * @param city Cidade do endereço do usuário.
     * @param postalCode Código postal do endereço do usuário.
     * @param phone Telefone de contato do usuário.
     * @param password Senha para autenticação do usuário.
     * @return Boolean indicando se o registro foi bem-sucedido ou não.
     */
    fun register(
        name: String, email: String, nif: String, dateOfBirth: LocalDate,
        street: String, number: Int, city: String, postalCode: String,
        phone: String, password: String, callback: (Boolean) -> Unit
    ) {

        if (!userValidator.isValidName(name) ||
            !userValidator.isValidEmail(email) ||
            !userValidator.isValidNIF(nif) ||
            !userValidator.isValidDateOfBirth(dateOfBirth) ||
            !userValidator.isValidPortuguesePhone(phone)
        ) {
            _registrationError.value = "Usuário Inválido"
            callback(false)
            return
        }

        if (!addressValidator.isValidCity(city) ||
            !addressValidator.isValidNumber(number) ||
            !addressValidator.isValidStreet(street) ||
            !addressValidator.isValidPostalCode(postalCode)
        ) {
            _registrationError.value = "Morada Inválida"
            callback(false)
            return
        }


        authRepository.register(email, password) { userId ->
            if (userId == null) {
                _registrationError.value = "Falha ao registrar usuário"
                callback(false)
                return@register
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


            userRepository.createUser(userId, serviceUser) { success ->
                if (success == true) {
                    _navigateToActivity.value = UserActivity::class.java
                    callback(true)
                } else {
                    _registrationError.value = "Falha ao salvar informações do usuário"
                    callback(false)
                }

            }
        }
    }
}


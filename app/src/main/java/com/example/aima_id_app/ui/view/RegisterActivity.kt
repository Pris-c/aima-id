package com.example.aima_id_app.ui.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aima_id_app.R
import com.example.aima_id_app.data.model.db_model.ServiceUser
import com.example.aima_id_app.data.model.submodel.Address
import com.example.aima_id_app.data.repository.AuthRepository
import com.example.aima_id_app.data.repository.UserRepository
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseApp.*
import com.google.firebase.initialize
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var nifInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var birthDateInput: EditText
    private lateinit var cityInput: EditText
    private lateinit var streetInput: EditText
    private lateinit var numberInput: EditText
    private lateinit var postalCodeInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var passwordConfirmInput: EditText
    private lateinit var registerButton: Button
    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository
    private lateinit var address: Address

    /**
     * Method called when the activity is created.
     *
     * Initializes the user interface, configures input fields,
     * sets listeners for input validation when losing focus and
     * handles navigation when clicking the register button.
     *
     * @param savedInstanceState The state of the activity, if it exists. Used to restore the activity from a previous state.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_register)

        // Set up views
        nameInput = findViewById(R.id.name_input)
        nifInput = findViewById(R.id.nif_input)
        emailInput = findViewById(R.id.email_input)
        phoneInput = findViewById(R.id.phone_input)
        birthDateInput = findViewById(R.id.birthDate_input)
        cityInput = findViewById(R.id.city_input)
        streetInput = findViewById(R.id.street_input)
        numberInput = findViewById(R.id.number_input)
        postalCodeInput = findViewById(R.id.postalCode_input)
        passwordInput = findViewById(R.id.password_input)
        passwordConfirmInput = findViewById(R.id.passwordConfirm_input)
        registerButton = findViewById(R.id.register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up DatePicker for birthDate input
        birthDateInput.setOnClickListener {
            showDatePicker()
        }

        // Focus change listeners for validation on losing focus
        nameInput.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateName(nameInput.text.toString().trim()) }
        nifInput.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateNIF(nifInput.text.toString().trim()) }
        emailInput.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateEmail(emailInput.text.toString().trim()) }
        phoneInput.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validatePhone(phoneInput.text.toString().trim()) }
        birthDateInput.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateBirthDate(birthDateInput.text.toString().trim()) }
        cityInput.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateCity(cityInput.text.toString().trim()) }
        streetInput.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateStreet(streetInput.text.toString().trim()) }
        numberInput.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateNumber(numberInput.text.toString().trim()) }
        postalCodeInput.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validatePostalCode(postalCodeInput.text.toString().trim()) }
        passwordInput.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validatePassword(passwordInput.text.toString().trim()) }
        passwordConfirmInput.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validatePasswordConfirm(passwordConfirmInput.text.toString().trim()) }


        registerButton.setOnClickListener {
            if (validateInputs()) {
                val name = nameInput.text.toString().trim()
                val nif = nifInput.text.toString().trim()
                val email = emailInput.text.toString().trim()
                val phone = phoneInput.text.toString().trim()
                val dateOfBirth = LocalDate.parse(birthDateInput.text.toString().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                val city = cityInput.text.toString().trim()
                val street = streetInput.text.toString().trim()
                val numberString = numberInput.text.toString().trim()
                val number = numberString.toIntOrNull() ?: 0
                val postalCode = postalCodeInput.text.toString().trim()
                val password = passwordInput.text.toString().trim()

                val address = Address(city = city, street = street, number = number, postalCode = postalCode)

                val serviceUser = ServiceUser(email = email, nif = nif, name = name, dateOfBirth = dateOfBirth, phone = phone, address = address)

                authRepository = AuthRepository()
                authRepository.register(email, password) { userId ->
                    if (userId != null) {

                        userRepository = UserRepository()
                        userRepository.createUser(userId, serviceUser) { success ->
                            if (success == true) {
                                Snackbar.make(findViewById(android.R.id.content), "Usuário registado com sucesso!", Snackbar.LENGTH_SHORT).show()

                                Handler(Looper.getMainLooper()).postDelayed({
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                }, 3000)
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), "Erro ao registar o usuário!", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "Erro ao registar o usuário!", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }

    /**
     * Displays a date selection dialog for the date of birth field.
     *
     * The user can select a date, which will be formatted as DD/MM/YYYY
     * and displayed in the date of birth entry field.
     */
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                birthDateInput.setText("$dayOfMonth/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    /**
     * Validates all input fields on the registration form.
     *
     * Checks that the name, NIF, email, telephone and date of birth fields
     * meet the defined validation criteria.
     *
     * @return True if all fields are valid, false otherwise.
     */
    private fun validateInputs(): Boolean {
        return validateName(nameInput.text.toString().trim()) &&
                validateNIF(nifInput.text.toString().trim()) &&
                validateEmail(emailInput.text.toString().trim()) &&
                validatePhone(phoneInput.text.toString().trim()) &&
                validateBirthDate(birthDateInput.text.toString().trim()) &&
                validateCity(cityInput.text.toString().trim()) &&
                validateStreet(streetInput.text.toString().trim()) &&
                validateNumber(numberInput.text.toString().trim()) &&
                validatePostalCode(postalCodeInput.text.toString().trim()) &&
                validatePassword(passwordInput.text.toString().trim()) &&
                validatePasswordConfirm(passwordConfirmInput.text.toString().trim())
    }

    /**
     * Validates the name provided by the user.
     *
     * The name must be between 3 and 150 characters.
     *
     * @param name The name to be validated.
     * @return True if the name is valid, false otherwise.
     */
    private fun validateName(name: String): Boolean {
        return if (name.length in 3..150) {
            nameInput.error = null
            true
        } else {
            nameInput.error = "Nome inválido. Deve ter entre 3 e 150 caracteres."
            false
        }
    }

    /**
     * Validates the Tax Identification Number (TIN) provided by the user.
     *
     * The NIF must have exactly 9 numeric digits.
     *
     * @param nif The NIF to be validated.
     * @return True if the NIF is valid, false otherwise.
     */
    private fun validateNIF(nif: String): Boolean {
        return if (nif.length == 9 && nif.all { it.isDigit() }) {
            nifInput.error = null
            true
        } else {
            nifInput.error = "NIF inválido. Deve conter 9 dígitos numéricos."
            false
        }
    }

    /**
     * Validates the email address provided by the user.
     *
     * The email must contain the "@" symbol.
     *
     * @param email The email to be validated.
     * @return True if the email is valid, false otherwise.
     */
    private fun validateEmail(email: String): Boolean {
        return if (email.contains("@")) {
            emailInput.error = null
            true
        } else {
            emailInput.error = "E-mail inválido."
            false
        }
    }

    /**
     * Validates the phone number provided by the user.
     *
     * Phone number must start with "+351", be exactly 13 characters long
     * and the remaining 9 digits must be numeric.
     *
     * @param phone The phone to be validated.
     * @return True if the phone number is valid, false otherwise.
     */
    private fun validatePhone(phone: String): Boolean {
        return if (phone.startsWith("+351") && phone.length == 13 && phone.substring(4).all { it.isDigit() }) {
            phoneInput.error = null
            true
        } else {
            phoneInput.error = "Telefone inválido. Deve conter DDI português + 9 digitos"
            false
        }
    }

    /**
     * Validates the date of birth provided by the user.
     *
     * The date must be in the format DD/MM/YYYY.
     *
     * @param date The date to be validated.
     * @return True if the date is valid, false otherwise.
     */
    private fun validateBirthDate(date: String): Boolean {
        return if (date.matches(Regex("\\d{1,2}/\\d{1,2}/\\d{4}"))) {
            birthDateInput.error = null
            true
        } else {
            birthDateInput.error = "Data de nascimento inválida. Formato deve ser DD/MM/AAAA."
            false
        }
    }

    /**
     * Validates the city provided by the user.
     *
     * The city must be between 3 and 150 characters.
     *
     * @param city The city to be validated.
     * @return True if the city is valid, false otherwise.
     */
    private fun validateCity(city: String): Boolean {
        return if (city.length in 3..150) {
            cityInput.error = null
            true
        } else {
            cityInput.error = "Nome do concelho inválido. Deve conter no mínimo 3 caracteres."
            false
        }
    }

    /**
     * Validates the street provided by the user.
     *
     * The street must be between 3 and 150 characters.
     *
     * @param street The street to be validated.
     * @return True if the street is valid, false otherwise.
     */
    private fun validateStreet(street: String): Boolean {
        return if (street.length in 2..150) {
            streetInput.error = null
            true
        } else {
            streetInput.error = "Nome do concelho inválido. Deve conter no mínimo 2 caracteres."
            false
        }
    }

    /**
     * Validates the number adress provided by the user.
     *
     * The number must be between 1 and 15 characters.
     *
     * @param number The number to be validated.
     * @return True if the number is valid, false otherwise.
     */
    private fun validateNumber(number: String): Boolean {
        return if (number.isNotEmpty() && number.all { it.isDigit() }) {
            numberInput.error = null
            true
        } else {
            numberInput.error = "Deve conter no mínimo 1 numero"
            false
        }
    }

    /**
     * Validates the postal code provided by the user.
     *
     * The postal code must have min 7 digits.
     *
     * @param postalCode The postalCode to be validated.
     * @return True if the postalCode is valid, false otherwise.
     */
    private fun validatePostalCode(postalCode: String): Boolean {
        return if (postalCode.length >= 7 && postalCode.all { it.isDigit() }) {
            postalCodeInput.error = null
            true
        } else {
            postalCodeInput.error = "Codigo postal inválido. Deve conter no mínimo 7 dígitos."
            false
        }
    }

    /**
     * Validates the password provided by the user.
     *
     * The password must contain at least 6 characters to be considered valid.
     *
     * @param password The password to be validated.
     * @return True if the password is valid, false otherwise.
     */
    private fun validatePassword(password: String): Boolean {
        return if (password.length >= 6 ) {
            passwordInput.error = null
            true
        } else {
            passwordInput.error = "A senha deve conter no mínimo 6 dígitos."
            false
        }
    }

    /**
     * Validates the password confirmation provided by the user.
     *
     * To be valid, password confirmation must:
     * - Not empty
     * - Be exactly the same as the original password provided in the password field
     *
     * @param passwordConfirm The confirmation password to be validated.
     * @return True if the password confirmation is valid and the same as the original password, false otherwise.
     *
     * @see validatePassword
     */
    private fun validatePasswordConfirm(passwordConfirm: String): Boolean {
        val originalPassword = passwordInput.text.toString().trim()

        return when {
            passwordConfirm.isEmpty() -> {
                passwordConfirmInput.error = "A confirmação de senha é obrigatória"
                false
            }
            passwordConfirm != originalPassword -> {
                passwordConfirmInput.error = "As senhas não coincidem"
                false
            }
            else -> {
                passwordConfirmInput.error = null
                true
            }
        }
    }

}
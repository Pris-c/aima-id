package com.example.aima_id_app.ui.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aima_id_app.R
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var nifInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var birthDateInput: EditText
    private lateinit var registerButton: Button

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
        birthDateInput = findViewById(R.id.birthDateEditText)
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


        registerButton.setOnClickListener {
            if (validateInputs()) {
                /*val name = nameInput.text.toString().trim()
                val nif = nifInput.text.toString().trim()
                val email = emailInput.text.toString().trim()
                val phone = phoneInput.text.toString().trim()
                val birthDate = LocalDate.parse(birthDateInput.text.toString().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                val newUser = User(role = UserRole.CLIENT, email = email, nif = nif, phone = phone, name = name, dateOfBirth = birthDate)

                registerViewModel.registerUser(newUser)*/

                showSuccessMessage()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
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
                validateBirthDate(birthDateInput.text.toString().trim())
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
            emailInput.error = "E-mail inválido. Deve conter '@'."
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
            phoneInput.error = "Telefone inválido. Deve conter 9 dígitos numéricos e começar com +351."
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
     * Displays a success message when user registration is completed successfully.
     *
     * The message is displayed at the bottom of the screen using a Snackbar.
     */
    private fun showSuccessMessage() {
        Snackbar.make(findViewById(android.R.id.content), "Usuário registrado com sucesso!", Snackbar.LENGTH_SHORT).show()
    }
}
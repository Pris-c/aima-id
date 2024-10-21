package com.example.aima_id_app.ui.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.aima_id_app.R
import com.example.aima_id_app.ui.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var nifInput: EditText
    private lateinit var emailInput: EditText

    /**
     * Method called when the activity is created.
     *
     * Initializes the user interface, configures event listeners, and watches the ViewModel for changes.
     * Here, the NIF and email input fields are also configured, as well as handling navigation
     * between activities, input validations and click events on login and registration buttons.
     *
     * @param savedInstanceState The state of the activity, if it exists. Used to restore the activity from a previous state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val registerButton: Button = findViewById(R.id.register)
        val loginButton: Button = findViewById(R.id.login)
        nifInput = findViewById(R.id.nif_input)
        emailInput = findViewById(R.id.email_input)

        // Watch for changes in LiveData to redirect the user to the correct Activity
        loginViewModel.navigateToActivity.observe(this, Observer {
            it?.let {
                startActivity(Intent(this, it))
                finish()
            }
        })

        // Observe login failures
        loginViewModel.errorMessage.observe(this, Observer { error ->
            error?.let { showError(it) }
        })

        // Configure login event
        loginButton.setOnClickListener {
            val nif = nifInput.text.toString().trim()
            val email = emailInput.text.toString().trim()

            if (validateNIF(nif) && validateEmail(email)) {
                loginViewModel.login(nif, email)
            }
        }

        // Validate NIF when the input field loses focus
        nifInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // Check if the field has lost focus
                validateNIF(nifInput.text.toString().trim())
            }
        }

        // Validate email when the input field loses focus
        emailInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // Check if the field has lost focus
                validateEmail(emailInput.text.toString().trim())
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Displays an error message using a Snackbar.
     *
     * The error message is displayed at the bottom of the screen for a short period.
     *
     * @param message The error message to display.
     */
    private fun showError(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }

    /**
     * Validates the NIF (Tax Identification Number) provided.
     *
     * Checks whether the NIF contains exactly 9 numeric digits. If the NIF is valid, removes any error messages associated with the NIF input field.
     * Otherwise, display an error message in the field.
     *
     * @param nif The NIF to be validated.
     * @return `true` if the NIF is valid (contains exactly 9 digits), `false` otherwise.
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
     * Validates the email provided.
     *
     * Checks if the email contains the '@' symbol. If the email is valid, removes any error messages associated with the email input field.
     * Otherwise, display an error message in the field.
     *
     * @param email The email to be validated.
     * @return `true` if the email is valid (contains '@'), `false` otherwise.
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
}
package com.example.aima_id_app.ui.view
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.aima_id_app.R
import com.example.aima_id_app.ui.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val registerButton: Button = findViewById(R.id.register)
        val loginButton: Button = findViewById(R.id.login)
        val nifInput: TextInputEditText = findViewById(R.id.nif_input)
        val emailInput: TextInputEditText = findViewById(R.id.email_input)

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

    private fun showError(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun validateNIF(nif: String): Boolean {
        return if (nif.length == 9 && nif.all { it.isDigit() }) {
            true
        } else {
            showError("NIF inválido. Deve conter 9 dígitos.")
            false
        }
    }

    private fun validateEmail(email: String): Boolean {
        return if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            true
        } else {
            showError("Formato de e-mail inválido.")
            false
        }
    }
}
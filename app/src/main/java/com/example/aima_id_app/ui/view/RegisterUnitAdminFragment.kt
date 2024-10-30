package com.example.aima_id_app.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.aima_id_app.R
import com.example.aima_id_app.ui.viewmodel.AimaUnitRegisterViewModel
import com.google.android.material.snackbar.Snackbar

class RegisterUnitAdminFragment : Fragment() {

    private lateinit var nameUnitInput: EditText
    private lateinit var cityUnitInput: EditText
    private lateinit var streetUnitInput: EditText
    private lateinit var numberUnitInput: EditText
    private lateinit var postalCodeUnitInput: EditText
    private lateinit var longitudeUnitInput: EditText
    private lateinit var latitudeUnitInput: EditText
    private lateinit var registerButton: Button
    private lateinit var aimaUnitRegisterViewModel: AimaUnitRegisterViewModel


    /**
     * Initializes the fragment view and sets observers for ViewModel error messages.
     * Inflates the fragment layout and returns the created view.
     *
     * @param inflater The LayoutInflater used to inflate the view.
     * @param container The parent ViewGroup of the view.
     * @param savedInstanceState The saved state of the fragment.
     * @return The view of the fragment.
     */
    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View? {

        aimaUnitRegisterViewModel = ViewModelProvider(this)[AimaUnitRegisterViewModel::class.java]


        fun showError(message: String) {
            Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
        }


        // Observe errors
        aimaUnitRegisterViewModel.geolocationErrorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let { showError(it) }
        })

        aimaUnitRegisterViewModel.addressErrorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let { showError(it) }
        })

        aimaUnitRegisterViewModel.conflictErrorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let { showError(it) }
        })

        aimaUnitRegisterViewModel.createAimaUnitMessage.observe(viewLifecycleOwner, Observer { message ->
            message?.let { showError(it) }
        })


        return inflater.inflate(R.layout.fragment_register_unit_admin, container, false)
    }



    /**
     * Validates all input fields on the registration form.
     *
     * Checks that the name, city, street, number, postal code, longitude, latitude, fields
     * meet the defined validation criteria.
     *
     * @return True if all fields are valid, false otherwise.
     */
    private fun validateInputs(): Boolean {
        return validateName(nameUnitInput.text.toString().trim()) &&
                validateCity(cityUnitInput.text.toString().trim()) &&
                validateStreet(streetUnitInput.text.toString().trim()) &&
                validateNumber(numberUnitInput.text.toString().trim()) &&
                validatePostalCode(postalCodeUnitInput.text.toString().trim()) &&
                validateLatitude(latitudeUnitInput.text.toString().trim()) &&
                validateLongitude(longitudeUnitInput.text.toString().trim())
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
            nameUnitInput.error = null
            true
        } else {
            nameUnitInput.error = "Nome inválido. Deve ter entre 3 e 150 caracteres."
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
        return if (city.isNotEmpty()) {
            cityUnitInput.error = null
            true
        } else {
            cityUnitInput.error = "Selecione uma unidade."
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
            streetUnitInput.error = null
            true
        } else {
            streetUnitInput.error = "Nome do concelho inválido. Deve conter no mínimo 2 caracteres."
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
            numberUnitInput.error = null
            true
        } else {
            numberUnitInput.error = "Deve conter no mínimo 1 numero"
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
            postalCodeUnitInput.error = null
            true
        } else {
            postalCodeUnitInput.error = "Codigo postal inválido. Deve conter no mínimo 7 dígitos."
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
    private fun validateLatitude(latitute: String): Boolean {
        return if (latitute.isNotEmpty() && latitute.all { it.isDigit() }) {
            latitudeUnitInput.error = null
            true
        } else {
            latitudeUnitInput.error = "Deve conter no mínimo 1 numero"
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
    private fun validateLongitude(longitute: String): Boolean {
        return if (longitute.isNotEmpty() && longitute.all { it.isDigit() }) {
            longitudeUnitInput.error = null
            true
        } else {
            longitudeUnitInput.error = "Deve conter no mínimo 1 numero"
            false
        }
    }

    /**
     * Called immediately after the view of the fragment has been created.
     *
     * This method initializes UI elements, sets up focus change listeners for input validation,
     * and handles the registration process when the register button is clicked.
     *
     * @param view The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as described by the parameter.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)

        nameUnitInput = view.findViewById(R.id.nameUnit_input)
        cityUnitInput = view.findViewById(R.id.cityUnit_input)
        streetUnitInput = view.findViewById(R.id.streetUnit_input)
        numberUnitInput = view.findViewById(R.id.numberUnit_input)
        postalCodeUnitInput = view.findViewById(R.id.postalCodeUnit_input)
        longitudeUnitInput = view.findViewById(R.id.longitude_input)
        latitudeUnitInput = view.findViewById(R.id.latitude_input)
        registerButton = view.findViewById(R.id.register)


        // Focus change listeners para validação ao perder o foco
        nameUnitInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateName(
                nameUnitInput.text.toString().trim()
            )
        }
        cityUnitInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateCity(
                cityUnitInput.text.toString().trim()
            )
        }
        streetUnitInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateStreet(
                streetUnitInput.text.toString().trim()
            )
        }
        numberUnitInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateNumber(
                numberUnitInput.text.toString().trim()
            )
        }
        postalCodeUnitInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validatePostalCode(
                postalCodeUnitInput.text.toString().trim()
            )
        }
        latitudeUnitInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateLatitude(
                latitudeUnitInput.text.toString().trim()
            )
        }
        longitudeUnitInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateLongitude(
                longitudeUnitInput.text.toString().trim()
            )
        }


        registerButton.setOnClickListener {

            if (validateInputs()) {
                val name = nameUnitInput.text.toString().trim()
                val city = cityUnitInput.text.toString().trim()
                val street = streetUnitInput.text.toString().trim()
                val number = numberUnitInput.text.toString().trim().toIntOrNull() ?: 0
                val postalCode = postalCodeUnitInput.text.toString().trim()
                val latitude = latitudeUnitInput.text.toString().trim().toDoubleOrNull() ?: 0.0
                val longitude = longitudeUnitInput.text.toString().trim().toDoubleOrNull() ?: 0.0


                aimaUnitRegisterViewModel.register(name, street, number, city, postalCode, latitude, longitude) { success ->
                    Handler(Looper.getMainLooper()).postDelayed({
                        requireActivity().supportFragmentManager.popBackStack()
                    }, 3000)
                }
            }
        }

    }
}

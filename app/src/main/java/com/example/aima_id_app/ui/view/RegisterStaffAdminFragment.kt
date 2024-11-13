package com.example.aima_id_app.ui.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.aima_id_app.R
import com.example.aima_id_app.ui.viewmodel.AimaUnitRegisterViewModel
import com.example.aima_id_app.ui.viewmodel.StaffRegisterViewModel
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar


class RegisterStaffAdminFragment : Fragment() {

    private lateinit var nameStaffInput: EditText
    private lateinit var nifStaffInput: EditText
    private lateinit var emailStaffInput: EditText
    private lateinit var birthDateStaffInput: EditText
    private lateinit var workUnitInput: Spinner
    private lateinit var registerButton: Button
    private lateinit var staffRegisterViewModel: StaffRegisterViewModel
    private lateinit var aimaUnitRegisterViewModel: AimaUnitRegisterViewModel

    /**
     * Initializes the fragment view, configures the ViewModel and observes error messages.
     * Inflates the fragment layout and returns the created view.
     *
     * @param inflater The LayoutInflater used to inflate the view.
     * @param container The parent ViewGroup of the view.
     * @param savedInstanceState The saved state of the fragment.
     * @return The view of the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        staffRegisterViewModel = ViewModelProvider(this)[StaffRegisterViewModel::class.java]

        /**
         * Displays an error message to the user using a Snackbar.
         *
         * @param message The error message to display.
         */
        fun showError(message: String) {
           Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
        }

        staffRegisterViewModel.nameErrorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let { showError(it) }
        })
        staffRegisterViewModel.registerUserMessage.observe(viewLifecycleOwner) { message ->
            if (message == "Funcionário registado com sucesso") {
                val email = staffRegisterViewModel.staffLogin.value
                val password = staffRegisterViewModel.staffPassword.value
                if (email != null && password != null) {
                    val snackbarMessage = "Login: $email\nPassword: $password"
                    Snackbar.make(requireView(), snackbarMessage, Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK") {  }
                        .show()
                }
            } else {

            }
        }
        staffRegisterViewModel.aimaUnitErrorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let { showError(it) }
        })
        staffRegisterViewModel.birthdateErrorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let { showError(it) }
        })
        staffRegisterViewModel.conflictErrorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let { showError(it) }
        })
        staffRegisterViewModel.nifErrorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let { showError(it) }
        })



        return inflater.inflate(R.layout.fragment_register_staff_admin, container, false)
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
        return validateName(nameStaffInput.text.toString().trim()) &&
                validateNIF(nifStaffInput.text.toString().trim()) &&
                validateEmail(emailStaffInput.text.toString().trim()) &&
                validateBirthDate(birthDateStaffInput.text.toString().trim())

    }


    /**
     * Displays a date selection dialog for the date of birth field.
     *
     * The user can select a date, which will be formatted as DD/MM/YYYY
     * and displayed in the date of birth entry field.
     */
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireActivity(),
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                birthDateStaffInput.setText(selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
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
            nameStaffInput.error = null
            true
        } else {
            nameStaffInput.error = "Nome inválido. Deve ter entre 3 e 150 caracteres."
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
            nifStaffInput.error = null
            true
        } else {
            nifStaffInput.error = "NIF inválido. Deve conter 9 dígitos numéricos."
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
            emailStaffInput.error = null
            true
        } else {
            emailStaffInput.error = "E-mail inválido."
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
            birthDateStaffInput.error = null
            true
        } else {
            birthDateStaffInput.error = "Data de nascimento inválida. Formato deve ser DD/MM/AAAA."
            false
        }
    }


    /**
     * Initializes the fragment view, configures listeners for input validation,
     * watches ViewModel error messages and configures the registration button.
     *
     * @param view The View returned by onCreateView.
     * @param savedInstanceState If not null, this fragment is being rebuilt
     * from a previous saved state as described by the parameter.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameStaffInput = view.findViewById(R.id.nameStaff_input)
        nifStaffInput = view.findViewById(R.id.nifStaff_input)
        emailStaffInput = view.findViewById(R.id.emailStaff_input)
        birthDateStaffInput = view.findViewById(R.id.birthDateStaff_input)
        workUnitInput = view.findViewById(R.id.workUnit_input)
        registerButton = view.findViewById(R.id.registerStaffButton)

        birthDateStaffInput.setOnClickListener {
            showDatePicker()
        }

        nameStaffInput.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateName(nameStaffInput.text.toString().trim()) }
        nifStaffInput.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateNIF(nifStaffInput.text.toString().trim()) }
        emailStaffInput.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateEmail(emailStaffInput.text.toString().trim()) }
        birthDateStaffInput.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateBirthDate(birthDateStaffInput.text.toString().trim()) }


        staffRegisterViewModel =
            ViewModelProvider(this).get(StaffRegisterViewModel::class.java)

        staffRegisterViewModel.nameErrorMessage.observe(viewLifecycleOwner) { message ->
            nameStaffInput.error = message
        }

        staffRegisterViewModel.nifErrorMessage.observe(viewLifecycleOwner) { message ->
            nifStaffInput.error = message
        }

        staffRegisterViewModel.birthdateErrorMessage.observe(viewLifecycleOwner) { message ->
            birthDateStaffInput.error = message
        }

        staffRegisterViewModel.registerUserMessage.observe(viewLifecycleOwner) { message ->
            if (message == "Funcionário registado com sucesso") {
                val email = staffRegisterViewModel.staffLogin.value
                val password = staffRegisterViewModel.staffPassword.value
                if (email != null && password != null) {
                    val snackbarMessage = "Login: $email\nPassword: $password"
                    Snackbar.make(requireView(), snackbarMessage, Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK") {  }
                        .show()

                }
            }
        }


        aimaUnitRegisterViewModel = ViewModelProvider(this).get(AimaUnitRegisterViewModel::class.java)
        aimaUnitRegisterViewModel.fetchAimaUnits()

        aimaUnitRegisterViewModel.aimaUnitsMap.observe(viewLifecycleOwner) { unitsMap ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, unitsMap.values.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            workUnitInput.adapter = adapter

            registerButton.setOnClickListener {
                if (validateInputs()) {
                    val name = nameStaffInput.text.toString().trim()
                    val nif = nifStaffInput.text.toString().trim()
                    val email = emailStaffInput.text.toString().trim()
                    val dateOfBirth = LocalDate.parse(birthDateStaffInput.text.toString().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))


                    val selectedAimaUnitPosition = workUnitInput.selectedItemPosition
                    val selectedAimaUnitId = unitsMap.keys.toList().getOrNull(selectedAimaUnitPosition)

                    if (selectedAimaUnitId != null) {
                        staffRegisterViewModel.register(name, email, nif, dateOfBirth, selectedAimaUnitId)

                        Handler(Looper.getMainLooper()).postDelayed({
                            requireActivity().supportFragmentManager.popBackStack()
                        }, 3000)

                    } else {
                        Snackbar.make(view, "Selecione uma unidade válida.", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }


}
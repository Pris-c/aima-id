package com.example.aima_id_app.util.validators

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.temporal.ChronoUnit


/**
 * Utility class for validating user input, such as email, NIF, name, date of birth, and phone.
 */
class UserValidator{
    /**
     * Function that validates if an email is valid.
     *
     * @param target The character sequence to be checked (the email).
     * @return Returns true if the email is not null, not empty, and matches the email format pattern.
     */
    fun isValidEmail(target: CharSequence?): Boolean {
        return !target.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(target)
            .matches()
    }

    /**
     * Function that validates if a name is valid.
     *
     * @param name The string representing the name of the user.
     * @return Returns true if the name has between 3 and 150 characters.
     */
    fun isValidName(name: String): Boolean {
        return name.length in 3..150
    }

    /**
     * Function that validates if a Portuguese NIF (Tax Identification Number) is valid.
     *
     * @param nif The string representing the NIF number.
     * @return Returns true if the NIF has exactly 9 digits and all are numeric.
     */
    fun isValidNIF(nif: String): Boolean {
        return nif.length == 9 && nif.all { it.isDigit() }
    }

    /**
     * Function that validates if a Portuguese phone number is valid.
     *
     * @param phone The character sequence to be checked (the phone number).
     * @return Returns true if the phone is not null or not empty, matches the phone number format,
     * has exactly 9 digits, and starts with '9' (indicating a Portuguese mobile phone number).
     */
    fun isValidPortuguesePhone(phone: CharSequence?): Boolean {
        return !phone.isNullOrEmpty() && android.util.Patterns.PHONE.matcher(phone)
            .matches() && phone.length == 9 && (phone.startsWith("9"))
    }

    /**
     * Function that validates if a date of birth is valid.
     * It checks if the date is in the past and if the age is between 0 and 130 years.
     *
     * @RequiresApi This function requires API 26 (Oreo) or higher to work correctly.
     *
     * @param dateOfBirth The date of birth as a LocalDate object.
     * @return Returns true if the date of birth is before today and the age is between 0 and 130 years.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun isValidDateOfBirth(dateOfBirth: LocalDate): Boolean {

        val today = LocalDate.now()
        if (!dateOfBirth.isBefore(today)){
            return false
        }
        val age = ChronoUnit.YEARS.between(dateOfBirth, today)

        return age in 0..130
    }

}
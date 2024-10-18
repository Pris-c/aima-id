package util.enums.validators

import java.time.LocalDate


/**
 * Utility class for validating user input, such as email, NIF, name, date of birth, and phone.
 */
class UserValidator(private val email: String,
                    private val phone: String,
                    private val name: String,
                    private val nif: String,
                    private val dateOfBirth: LocalDate
) {


}
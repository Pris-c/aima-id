package com.example.aima_id_app.data.model.db_model

import com.example.aima_id_app.util.enums.UserRole
import java.time.LocalDate

/**
 * Represents a Staff User, inheriting properties and methods from the User class.
 *
 * @property role The role of the user, which should be set to STAFF for this class.
 * @property email The email address of the staff user.
 * @property nif The NIF (tax identification number) of the staff user.
 * @property name The name of the staff user.
 * @property dateOfBirth The date of birth of the staff user.
 * @property aimaUnitId The AIMA unit ID associated with the staff user.
 */
class StaffUser(
    email: String = "",
    nif: String = "",
    name: String = "",
    dateOfBirth: String = LocalDate.of(1,1,1).toString(),
    val aimaUnitId: String = ""
) :
    User (UserRole.STAFF.role, email, nif , name, dateOfBirth)

package com.example.aima_id_app.data.model.db_model

import com.example.aima_id_app.util.enums.UserRole
import java.time.LocalDate

/**
 * Represents an Admin User, inheriting properties and methods from the User class.
 *
 * @property role The role of the user, which should be set to ADMIN for this class.
 * @property email The email address of the admin user.
 * @property nif The NIF (tax identification number) of the admin user.
 * @property name The name of the admin user.
 * @property dateOfBirth The date of birth of the admin user.
 */
class AdminUser(email: String, nif: String, name: String, dateOfBirth: LocalDate) :
    User (UserRole.ADMIN, email, nif , name, dateOfBirth)



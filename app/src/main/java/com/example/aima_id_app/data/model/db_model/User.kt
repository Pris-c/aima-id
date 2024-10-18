package com.example.aima_id_app.data.model.db_model

import util.enums.UserRole
import java.time.LocalDate

/**
 * Represents a User with basic personal information.
 *
 * @property role The role of the user (e.g., ADMIN, SERVICE_USER, STAFF).
 * @property email The email address of the user.
 * @property nif The NIF (tax identification number) of the user.
 * @property name The name of the user.
 * @property dateOfBirth The date of birth of the user.
 */
open class User (
     val role: UserRole,
     val email: String,
     val nif: String,
     val name: String,
     val dateOfBirth: LocalDate,
)
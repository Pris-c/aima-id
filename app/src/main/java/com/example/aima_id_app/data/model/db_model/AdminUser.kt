package com.example.aima_id_app.data.model.db_model

import util.userRole
import java.util.Date

/**
 * Represents an Admin User, inheriting properties and methods from the User class.
 *
 * @property role The role of the user, which should be set to ADMIN for this class.
 * @property email The email address of the admin user.
 * @property nif The NIF (tax identification number) of the admin user.
 * @property name The name of the admin user.
 * @property dateOfBirth The date of birth of the admin user.
 */
class AdminUser (role: userRole, email: String, nif: String, name: String, dateOfBirth: Date) :
    User (userRole.ADMIN, email, nif , name, dateOfBirth)
{
    /**
     * Displays the details of the admin user, including name, email, nif and date of birth.
     */
    override fun displayInfo() {
        super.displayInfo()
    }

    }


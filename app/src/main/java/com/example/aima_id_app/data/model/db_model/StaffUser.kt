package com.example.aima_id_app.data.model.db_model

import util.userRole
import java.util.Date

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
class StaffUser (role: userRole, email: String, nif: String, name: String, dateOfBirth: Date, val aimaUnitId: String ) :
    User (userRole.STAFF, email, nif , name, dateOfBirth)
{
    /**
     * Displays the details of the staff user, including name, email, nif, date of birth and the assigned unit id.
     */
    override fun displayInfo() {
        super.displayInfo()
        println("Staff unit id: $aimaUnitId")
    }
}
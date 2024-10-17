package com.example.aima_id_app.data.model.db_model

import util.userRole
import java.util.Date

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
     val role: userRole,
     val email: String,
     val nif: String,
     val name: String,
     val dateOfBirth: Date,
){
     /**
      * Displays the details of the user, including name, role, email, nif and date of birth.
      */
     open fun displayInfo(){
          println("Name: $name \nnRole: $role \nEmail: $email \nNIF: $nif \nDOB: $dateOfBirth")
     }
}
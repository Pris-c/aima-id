package com.example.aima_id_app.data.model.db_model

import com.example.aima_id_app.data.model.submodel.Address
import com.example.aima_id_app.data.model.submodel.DocUser
import com.example.aima_id_app.util.enums.UserRole
import java.time.LocalDate

/**
 * Represents a Service User, inheriting properties and methods from the User class.
 *
 * @property role The role of the user, which should be set to SERVICE_USER for this class.
 * @property email The email address of the service user.
 * @property nif The NIF (tax identification number) of the service user.
 * @property name The name of the service user.
 * @property dateOfBirth The date of birth of the service user.
 * @property phone The phone number of the service user.
 * @property address The address of the service user.
 * @property docs A mutable list of documents associated with the service user.
 *                Defaults to an empty list if not provided.
 */
class ServiceUser(
    email: String = "",
    nif: String = "",
    name: String = "",
    dateOfBirth: LocalDate = LocalDate.of(1,1,1),
    val phone: String ="",
    val address: Address = Address(),
    val docs: MutableList<DocUser> = mutableListOf()
) :
    User (UserRole.SERVICE_USER, email, nif , name, dateOfBirth)

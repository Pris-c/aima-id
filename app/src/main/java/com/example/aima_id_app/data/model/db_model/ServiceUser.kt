package com.example.aima_id_app.data.model.db_model

import com.example.aima_id_app.data.model.submodel.Address
import com.example.aima_id_app.data.model.submodel.DocUser
import util.userRole
import java.util.Date

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
class ServiceUser(role: userRole, email: String, nif: String, name: String, dateOfBirth: Date,
                  val phone: String, val address: Address, val docs: MutableList<DocUser> = mutableListOf()) :
    User (userRole.SERVICE_USER, email, nif , name, dateOfBirth)

{
    /**
     * Displays the details of the service user, including phone number, address,
     * and the list of documents. If no documents are available, it informs the user.
     */
    override fun displayInfo() {
        super.displayInfo()
        println("Phone: $phone \nAddress: ${address.street} ${address.number} \n${address.city} Postal code: ${address.postalCode} ")
        println("Documents: ")
        if (docs.isNotEmpty()){
            for(doc in docs){
                println("Doc ID: ${doc.docId}| Doc Type: ${doc.docType}")
            }
        }else{
            println("No documents available.")
        }

    }
}
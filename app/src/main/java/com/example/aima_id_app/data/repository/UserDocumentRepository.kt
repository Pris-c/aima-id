package com.example.aima_id_app.data.repository

import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.util.enums.DocStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

/**
 * Repository class for managing document-related operations in the Firestore database.
 *
 * Provides methods for creating, finding, updating, and deleting user documents.
 */
class UserDocumentRepository {

    private val db = FirebaseFirestore.getInstance().collection("userDocuments")

    /**
     * Saves a new document entry in Firestore and returns the generated ID.
     *
     * @param doc The UserDocument object to be saved.
     * @param onComplete A callback function that returns the document ID if the save operation is successful, or null if it fails.
     */
    fun save(doc: UserDocument, onComplete: (String?) -> Unit) {
        db.add(doc)
            .addOnSuccessListener { documentReference ->
                onComplete(documentReference.id)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }


    /**
     * Finds a document by its ID in the database.
     *
     * @param id The ID of the document to be retrieved.
     * @param onComplete A callback function that returns the document if found, or null if the operation fails.
     */
    fun findById(id: String, onComplete: (UserDocument?) -> Unit) {
        db.document(id).get()
            .addOnSuccessListener { documentSnapshot ->
                val document = documentSnapshot.toObject<UserDocument>()
                onComplete(document)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    /**
     * Updates an existing document in the database with the given ID.
     *
     * @param id The ID of the document to be updated.
     * @param doc The updated UserDocument object.
     * @param onComplete A callback function that returns true if the operation is successful, false otherwise.
     */
    fun update(id: String, doc: UserDocument, onComplete: (Boolean?) -> Unit) {
        db.document(id).set(doc)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    /**
     * Deletes a document from the database by its ID.
     *
     * @param id The ID of the document to be deleted.
     * @param onComplete A callback function that returns true if the operation is successful, false otherwise.
     */
    fun delete(id: String, onComplete: (Boolean?) -> Unit) {
        db.document(id).delete()
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }


    /**
     * Retrieves UserDocuments associated with the specified user ID.
     *
     * @param Id The user ID to filter by.
     * @param onComplete A callback returning a list of UserDocument objects, or an empty list if none are found.
     */
    fun filterByUser(id: String, onComplete: (MutableList<UserDocument>) -> Unit) {
        db.whereEqualTo("userId", id).get()
            .addOnSuccessListener { result ->
                onComplete(result.mapNotNull { it.toObject<UserDocument>() }.toMutableList())
            }
            .addOnFailureListener {
                onComplete(mutableListOf())
            }
    }

    /**
     * Retrieves all UserDocuments with a "SUBMITTED" status.
     *
     * @param onComplete A callback returning a list of submitted UserDocument objects, or an empty list if none are found.
     */
    fun filterSubmittedDocs(onComplete: (MutableList<UserDocument>) -> Unit) {
        db.whereEqualTo("status", DocStatus.SUBMITTED).get()
            .addOnSuccessListener { result ->
                onComplete(result.mapNotNull { it.toObject<UserDocument>() }.toMutableList())
            }
            .addOnFailureListener {
                onComplete(mutableListOf())
            }
    }
}


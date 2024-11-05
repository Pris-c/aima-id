package com.example.aima_id_app.data.repository

import android.util.Log
import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.util.enums.DocStatus
import com.example.aima_id_app.util.enums.DocType
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
    fun update(id: String, doc: UserDocument, onComplete: (Boolean) -> Unit) {
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
    fun delete(id: String, onComplete: (Boolean) -> Unit) {
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
                if (!result.isEmpty){
                    onComplete(result.mapNotNull { it.toObject<UserDocument>() }.toMutableList())
                } else {
                    onComplete(mutableListOf())
                }
            }
            .addOnFailureListener {
                onComplete(mutableListOf())
            }
    }

    /**
     * Finds a document of a specific type associated with the given user ID.
     *
     * @param id The user ID to filter by.
     * @param docType The type of document to search for.
     * @param onComplete A callback returning the document ID and the UserDocument object if found, or nulls if not.
     */
    fun findDocTypeByUser(id: String, doctype: DocType, onComplete: (String?, UserDocument?) -> Unit){
        db.whereEqualTo("userId", id).whereEqualTo("docType", doctype.doc).get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty){
                    val document = result.documents[0]
                    onComplete(document.id, document.toObject<UserDocument>())
                } else {
                    onComplete(null, null)
                }
            }
            .addOnFailureListener {
                onComplete(null, null)
            }
    }

    /**
     * Retrieves documents associated with a specific user ID.
     *
     * @param id The user ID to filter by.
     * @param onComplete A callback returning a map of document IDs to UserDocument objects.
     */
    fun getDocumentsByUser(id: String, onComplete: (MutableMap<String, UserDocument>) -> Unit) {
        val documentsByUser = mutableMapOf<String, UserDocument>()
        db.whereEqualTo("userId", id).get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty){
                    for(doc in result){
                        documentsByUser[doc.id] = doc.toObject<UserDocument>()
                    }
                }
                onComplete(documentsByUser)
            }
            .addOnFailureListener {
                onComplete(mutableMapOf())
            }
    }


    /**
     * Retrieves all UserDocuments with a "Submetido" status from the database.
     *
     * @param onComplete A callback function that is invoked once the query completes. The callback receives a
     *                   `MutableMap<String, UserDocument>`, containing the documents with "SUBMITTED" status
     *                   (or an empty map if none are found or an error occurs).
     */
    fun filterSubmittedDocs(onComplete: (MutableMap<String, UserDocument>) -> Unit) {
        val submittedDocs = mutableMapOf<String, UserDocument>()

        db.whereEqualTo("status", DocStatus.SUBMITTED.status).get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty){
                    for (doc in result){
                        submittedDocs[doc.id] = doc.toObject<UserDocument>()
                    }
                }
                onComplete(submittedDocs)
            }
            .addOnFailureListener {
                onComplete(submittedDocs)
            }
    }

    /**
     * Retrieves the file name for a given document path from the database.
     *
     * @param path The path of the document in the database.
     * @param onComplete A callback that returns the file name as a String, or null if not found.
     */
    fun getFileName(path: String, onComplete: (String?) -> Unit) {
        db.whereEqualTo("docPath", path).get()
            .addOnSuccessListener { result ->

                if (result.documents.isNotEmpty()) {
                    val doc = result.documents.first().toObject<UserDocument>()

                    if (doc != null) {

                        val docName = "${doc.userId}_${doc.docType}"
                        onComplete(docName)
                    } else {

                        onComplete(null)
                    }
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }


    /**
     * Retrieves the document ID for a specific user ID and document type.
     *
     * @param userId The user ID to filter by.
     * @param docType The type of document to search for.
     * @param onComplete A callback that returns the document ID, or null if not found.
     */
    fun getDocumentId(userId: String, docType: DocType, onComplete: (String?) -> Unit) {
        db.whereEqualTo("userId", userId)
            .whereEqualTo("docType", docType)
            .get()
            .addOnSuccessListener { documents ->
                val docId = documents.documents.firstOrNull()?.id
                onComplete(docId)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }


}


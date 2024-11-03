package com.example.aima_id_app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.ServiceUser
import com.example.aima_id_app.data.model.db_model.User
import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.data.model.submodel.DocUser
import com.example.aima_id_app.data.repository.DocStorageRepository
import com.example.aima_id_app.data.repository.UserDocumentRepository
import com.example.aima_id_app.data.repository.UserRepository
import com.example.aima_id_app.util.enums.DocStatus
import com.example.aima_id_app.util.enums.DocType
import com.example.aima_id_app.util.enums.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.File
import java.time.LocalDate

/*
 * ViewModel for managing the upload of documents for an authenticated user.
 * This class communicates with repositories to save documents and update user information.
 */
class UploadDocsViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val userRepository: UserRepository = UserRepository(),
    private val storageRepository: DocStorageRepository = DocStorageRepository(),
    private val userDocumentRepository: UserDocumentRepository = UserDocumentRepository()
) : ViewModel() {

    private val _uploadStatus = MutableLiveData<String?>()
    val uploadStatus: LiveData<String?> get() = _uploadStatus

    /**
     * Saves a new document or updates an existing document for the authenticated user.
     * If the user already has a document of the same type, it updates the existing document.
     * Otherwise, it saves a new document.
     *
     * @param document The file to be saved.
     * @param docType The type of document to be checked and saved.
     * @return Returns the authenticated user or null if no user is authenticated.
     */
    fun saveOrUpdateDoc(document: File, docType: DocType): FirebaseUser? {
        val userId = auth.currentUser?.uid.toString()
        val user = auth.currentUser

        userRepository.findUserById(userId) { user ->
            if (user != null && user.role == UserRole.SERVICE_USER) {
                val serviceUser = user as? ServiceUser

                userDocumentRepository.filterByUser(userId) { userDocuments ->
                    val hasDocument = userDocuments.any { it.docType == docType }

                    if (!hasDocument) {
                        newDocUpdate(document, docType)
                    } else {
                        updateExistingDoc(document, userId, docType)
                    }
                }
            }
        }
        return auth.currentUser
    }

    /**
     * Saves a new document to Firebase Storage and creates an entry for it in the user's document repository.
     * Updates the user's document list with the new document if the user is a ServiceUser.
     *
     * @param document The file to be saved.
     * @param docType The type of the document to be saved.
     */
    private fun newDocUpdate(document: File, docType: DocType) {
        val userId = auth.currentUser?.uid ?: return
        val serviceUser = auth.currentUser as? ServiceUser

        storageRepository.saveFile(document) { docPath ->
            if (docPath != null) {
                val userDocument = UserDocument(
                    docPath = docPath,
                    docType = docType,
                    userId = userId,
                    submittedAt = LocalDate.now().toString()
                )

                userDocumentRepository.save(doc = userDocument) { docId ->
                    if (docId != null) {
                        val newDoc = DocUser(docType, docId)
                        serviceUser?.docs?.add(newDoc)

                        userRepository.updateUser(userId, user = User) { success ->
                            if (success == true) {
                                Log.d("UploadDocsViewModel", "Document added successfully.")
                            } else {
                                Log.e("UploadDocsViewModel", "Failed to update user.")
                            }
                        }
                    } else {
                        Log.e("UploadDocsViewModel", "Failed to save UserDocument.")
                    }
                }
            } else {
                Log.e("UploadDocsViewModel", "Failed to save file to storage.")
            }
        }
    }

    /**
     * Updates an existing document in Firebase Storage and in the user's document repository.
     * Retrieves the old document path, saves the new file, and deletes the old file.
     *
     * @param document The new file to be saved.
     * @param userId The ID of the user for whom the document is being updated.
     * @param docType The type of document being updated.
     */
    private fun updateExistingDoc(document: File, userId: String, docType: DocType) {
        val serviceUser = auth.currentUser as? ServiceUser

        userDocumentRepository.filterByUser(userId) { userDocuments ->
            val existingDocument = userDocuments.find { it.docType == docType }

            if (existingDocument != null) {
                val oldDocPath = existingDocument.docPath.toString()

                storageRepository.saveFile(document) { newDocPath ->
                    if (newDocPath != null) {
                        existingDocument.apply {
                            docPath = newDocPath
                            status = DocStatus.SUBMITTED
                            submittedAt = LocalDate.now().toString()
                        }

                        storageRepository.deleteFile(oldDocPath)
                        Log.d("UploadDocsViewModel", "Document updated successfully.")
                    } else {
                        Log.e("UploadDocsViewModel", "Failed to save new file for update.")
                    }
                }
            } else {
                Log.e("UploadDocsViewModel", "No existing document found for update.")
            }
        }
    }
}
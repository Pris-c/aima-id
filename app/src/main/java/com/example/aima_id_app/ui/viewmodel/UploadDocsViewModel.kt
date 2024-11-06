package com.example.aima_id_app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.data.repository.DocStorageRepository
import com.example.aima_id_app.data.repository.UserDocumentRepository
import com.example.aima_id_app.data.repository.UserRepository
import com.example.aima_id_app.util.enums.DocStatus
import com.example.aima_id_app.util.enums.DocType
import com.example.aima_id_app.util.enums.UserRole
import com.example.aima_id_app.util.validators.DocValidator
import com.google.firebase.auth.FirebaseAuth
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
    private val userDocumentRepository: UserDocumentRepository = UserDocumentRepository(),
    private val docValidator : DocValidator = DocValidator()
) : ViewModel() {

    private val _documentErrorMessage = MutableLiveData<String?>()
    val documentErrorMessage: LiveData<String?> get() = _documentErrorMessage

    /**
     * Saves a new document or updates an existing document for the authenticated user.
     *
     * If the user has an existing document of the specified type, it updates that document.
     * If not, it saves a new document.
     *
     * @param docFile The file to be saved or updated.
     * @param docType The type of document to check for existence and save.
     * @param onComplete A callback function that returns true if the operation was successful,
     *                   or false if it failed.
     */
    fun saveOrUpdateDoc(docFile: File, docType: DocType, status : String, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            _documentErrorMessage.postValue("User is not authenticated.")
            onComplete(false)
            return
        }

        if (!docValidator.isValidPdf(docFile)) {
            Log.d("SAVEDOC", "Documento invalido")
            onComplete(false)
            return
        }

        userRepository.findUserById(userId) { user ->
            if (user != null && (user.role == UserRole.SERVICE_USER.role)) {

                userDocumentRepository.findDocTypeByUser(userId, docType) { oldDocId, oldDocument ->
                    if (oldDocument != null && oldDocId != null) {
                        updateDocument(oldDocId, oldDocument, docFile){ updated ->
                            if (updated){
                                onComplete(true)
                            } else {
                                onComplete(false)
                            }
                        }

                    } else {
                        saveNewDocument(userId, docFile, docType, status) { saved ->
                            if (saved){
                                onComplete(true)
                            } else {
                                onComplete(false)
                            }
                        }
                    }
                }
            } else {
                _documentErrorMessage.postValue("Usuário inválido")
                onComplete(false)
            }
        }

    }

    /**
     * Saves a new document to Firebase Storage and creates an entry for it in the user's
     * document collection.
     *
     * @param userId The ID of the user who owns the document.
     * @param file The file to be saved.
     * @param docType The type of the document to be saved.
     * @param onComplete A callback function that indicates whether the save operation was successful.
     *                   Returns true if successful, false otherwise.
     */
    private fun saveNewDocument(userId: String, file: File, docType: DocType, status: String, onComplete: (Boolean) -> Unit) {
        Log.d("FILE", "Saving..")
        storageRepository.saveFile(file) { docPath ->
            if (docPath != null) {
                val userDocument = UserDocument(
                    docPath = docPath,
                    status = status,
                    docType = docType.doc,
                    userId = userId,
                    submittedAt = LocalDate.now().toString()
                )

                userDocumentRepository.save(userDocument) { docId ->
                    if (docId != null) {
                      onComplete(true)
                    } else {
                        _documentErrorMessage.postValue("Falha ao salvar documento.")
                    }
                }

            } else {
                _documentErrorMessage.postValue("Falha ao salvar arquivo.")
                onComplete(false)
            }
        }
    }

    /**
     * Updates an existing document by saving a new file to Firebase Storage, updating the document
     * fields, and deleting the old file from storage.
     *
     * This function attempts to save the provided new file to storage. If successful, it updates the
     * existing UserDocument with the new file path, submission date, and status.
     * After updating the document in the repository, it deletes the old file from storage.
     *
     * @param id The ID of the document to be updated.
     * @param document The existing UserDocument to update.
     * @param newFile The new file to upload and associate with the document.
     * @param onComplete A callback function that returns true if the update was successful,
     *                   or false if there was an error during the update operation.
     */
    private fun updateDocument(id: String, document: UserDocument, newFile: File, onComplete: (Boolean) -> Unit) {

        val oldFilePath = document.docPath

        storageRepository.saveFile(newFile) { newFilePath ->
            Log.d("FILE", "Updating..")
            if (newFilePath != null) {
                document.apply {
                    docPath = newFilePath
                    submittedAt = LocalDate.now().toString()
                    status = DocStatus.SUBMITTED.toString()
                }

                userDocumentRepository.update(id, document) { success ->
                    if (success){
                        storageRepository.deleteFile(oldFilePath){ deleted ->
                            if (!deleted){
                                _documentErrorMessage.postValue("Falha ao deletar arquivo antigo.")
                            }
                        }
                        onComplete(true)
                    } else {
                        _documentErrorMessage.postValue("Falha ao atualizar documento na base de dados.")
                        onComplete(false)
                    }
                }
            } else {
                _documentErrorMessage.postValue("Falha ao atualizar arquivo.")
                onComplete(false)
            }
        }
    }

}
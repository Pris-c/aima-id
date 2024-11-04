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

    private val _documentErrorMessage = MutableLiveData<String?>()
    val documentErrorMessage: LiveData<String?> get() = _documentErrorMessage

    /**
     * Saves a new document or updates an existing document for the authenticated user.
     * If the user already has a document of the same type, it updates the existing document.
     * Otherwise, it saves a new document.
     *
     * @param document The file to be saved.
     * @param docType The type of document to be checked and saved.
     * @return Returns the authenticated user or null if no user is authenticated.
     */
    fun saveOrUpdateDoc(document: File, docType: DocType): User? {
        val userId = auth.currentUser?.uid.toString()

        userRepository.findUserById(userId) { user ->
            if (user != null && (user.role).equals(UserRole.SERVICE_USER)) {

                userDocumentRepository.findDocTypeByUser(userId, docType) { id, userDocuments ->
                    if (userDocuments != null) {
                        updateDocument(userDocuments,document)
                    }else{
                        newDocUpdate(document, docType)
                    }
                }
            }else{
                _documentErrorMessage.postValue("Usuário inválido")
            }
        }

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
        val serviceUser = auth.currentUser as ServiceUser

        storageRepository.saveFile(document) { docPath ->
            if (docPath != null) {
                val userDocument = UserDocument(
                    docPath = docPath,
                    docType = docType.toString(),
                    userId = userId,
                    submittedAt = LocalDate.now().toString()
                )

                userDocumentRepository.save(doc = userDocument) { docId ->
                    if (docId != null) {
                        val newDoc = DocUser(docType, docId)
                        serviceUser.docs.add(newDoc)
                    } else {
                        _documentErrorMessage.postValue("Failed to save UserDocument.")
                    }
                }
            } else {
                _documentErrorMessage.postValue("Failed to save file to storage.")
            }
        }
    }
    /**
     * Updates an existing document by saving the new file to Firebase Storage, updating the document fields,
     * and deleting the old file from storage.
     *
     * @param document The existing UserDocument to update.
     * @param newFile The new file to upload.
     */
    fun updateDocument(document: UserDocument, newFile: File) {
        val oldDocPath = document.docPath

        storageRepository.saveFile(newFile) { newDocPath ->
            if (newDocPath != null) {
                document.apply {
                    docPath = newDocPath
                    submittedAt = LocalDate.now().toString()
                    status = DocStatus.SUBMITTED.toString()
                    expirationDate = LocalDate.of(2125, 12, 30).toString()
                }
                userDocumentRepository.getDocumentId(document.userId, document.docType) { docId ->
                    if (docId != null) {
                        userDocumentRepository.update(docId, document) { updateSuccess ->
                            if (updateSuccess!!) {
                                storageRepository.deleteFile(oldDocPath) { deleteSuccess ->
                                    if (deleteSuccess) {
                                        _documentErrorMessage.postValue("Documento atualizado e arquivo antigo deletado com sucesso.")
                                    } else {
                                        _documentErrorMessage.postValue("Falha ao deletar o arquivo antigo.")
                                    }
                                }
                            } else {
                                _documentErrorMessage.postValue("Falha ao atualizar o arquivo.")
                            }
                        }
                    } else {
                        _documentErrorMessage.postValue("ID do documento não encontrado.")
                    }
                }
            } else {
                _documentErrorMessage.postValue("Falha ao salvar o novo arquivo.")
            }
        }
    }
}
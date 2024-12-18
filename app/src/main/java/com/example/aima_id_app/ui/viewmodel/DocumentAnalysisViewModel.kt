package com.example.aima_id_app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.data.repository.UserDocumentRepository
import com.example.aima_id_app.util.enums.DocStatus
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.io.File
import java.time.LocalDate

/**
 * ViewModel to manage user document interactions and statuses.
 * Provides methods to retrieve, approve, reject, and update user-associated documents.
 *
 * @param userDocumentRepository Repository responsible for database operations related to user documents.
 * @param storageRepository Repository responsible for file storage operations, including upload and deletion.
 *
 * Properties:
 * - `_documentErrorMessage`: Private LiveData holding error or success messages for document interactions.
 * - `documentErrorMessage`: Public LiveData exposing messages for the UI to display to the user.
 */
class DocumentAnalysisViewModel(
    private val userDocumentRepository: UserDocumentRepository = UserDocumentRepository()

    ) : ViewModel(){

    private val _documentErrorMessage = MutableLiveData<String?>()
    val documentErrorMessage: LiveData<String?> get() = _documentErrorMessage

    /**
     * Approves the document by its ID. Fetches the document, updates its status to APPROVED,
     * sets the expiration date to six months from now, and notifies the user.
     *
     * @param docId The ID of the document to approve.
     */
    fun approveDoc(docId: String) {

        userDocumentRepository.findById(docId) { document ->
            if (document != null) {
                document.status = DocStatus.APPROVED.status
                document.expirationDate = LocalDate.now().plusMonths(6).toString()

                userDocumentRepository.update(docId, document) { updateSuccess ->
                    if (updateSuccess == true) {
                        _documentErrorMessage.postValue("Documento aprovado com sucesso!")
                    } else {
                        _documentErrorMessage.postValue("Falha ao aprovar o documento.")
                    }
                }
            } else {
                _documentErrorMessage.postValue("ID do documento não encontrado.")
            }
        }
    }

    /**
     * Rejects the document by its ID. Fetches the document, updates its status to REJECTED,
     * and notifies the user.
     *
     * @param docId The ID of the document to reject.
     */
    fun rejectDoc(docId: String) {
        userDocumentRepository.findById(docId) { document ->
            if (document != null) {
                document.status = DocStatus.REJECTED.status
                document.expirationDate = LocalDate.now().plusMonths(6).toString()

                userDocumentRepository.update(docId, document) { updateSuccess ->
                    if (updateSuccess == true) {
                        _documentErrorMessage.postValue("Documento aprovado com sucesso!")
                    } else {
                        _documentErrorMessage.postValue("Falha ao aprovar o documento.")
                    }
                }
            } else {
                _documentErrorMessage.postValue("ID do documento não encontrado.")
            }
        }
    }

    /**
     * Downloads a document from Firebase Storage.
     *
     * This function downloads the document specified by the `document` parameter
     * and saves it to a temporary file. The `onComplete` callback is invoked with
     * the absolute path of the downloaded file if the download is successful, or
     * with `null` if the download fails.
     *
     * @param document The UserDocument object containing the document path.
     * @param onComplete A callback function that is invoked when the download is complete.
     */
    fun downloadDocument(document: UserDocument, onComplete: (String?) -> Unit) {
        val fileName = document.docPath.substringAfterLast("_")

        val storageRef = Firebase.storage.reference.child(document.docPath)

        val localFile = File.createTempFile(fileName, ".pdf")

        storageRef.getFile(localFile).addOnSuccessListener {
            onComplete(localFile.absolutePath)
        }.addOnFailureListener { exception ->
            onComplete(null)
            Log.e("DocumentAnalysisViewModel", "Erro ao baixar documento: ${exception.message}")
        }
    }
}
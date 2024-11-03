package com.example.aima_id_app.ui.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.data.repository.DocStorageRepository
import com.example.aima_id_app.data.repository.UserDocumentRepository
import com.example.aima_id_app.util.enums.DocStatus
import java.io.File
import java.time.LocalDate


/**
 * ViewModel para gerenciar as interações com documentos do usuário.
 * Permite a recuperação e atualização de documentos associados a um usuário.
 *
 * @property storageRepository O repositório responsável por operações de armazenamento.
 * @property userDocumentRepository O repositório responsável por interações com documentos do usuário.
 */
class UserDocsViewModel(
    private val userDocumentRepository: UserDocumentRepository = UserDocumentRepository(),
    private val storageRepository: DocStorageRepository = DocStorageRepository()
) : ViewModel() {

    private val _documentErrorMessage = MutableLiveData<String?>()
    val documentErrorMessage: LiveData<String?> get() = _documentErrorMessage


    /**
     * Retrieves a mutable list of UserDocument entries associated with the specified user ID.
     * @param userId The ID of the user whose documents need to be retrieved.
     * @return MutableList of UserDocument entries.
     */
    fun getUserDocs(userId: String, onComplete: (MutableList<UserDocument>) -> Unit) {
        userDocumentRepository.filterByUser(userId) { userDocuments ->
            onComplete(userDocuments.ifEmpty { mutableListOf() })
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
                    status = DocStatus.SUBMITTED
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
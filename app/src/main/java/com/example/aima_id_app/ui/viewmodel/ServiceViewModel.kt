package com.example.aima_id_app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.AimaProcess
import com.example.aima_id_app.data.model.db_model.Service
import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.data.repository.AimaProcessRepository
import com.example.aima_id_app.data.repository.ServiceRepository
import com.example.aima_id_app.data.repository.UserDocumentRepository
import com.example.aima_id_app.util.enums.DocStatus
import com.example.aima_id_app.util.enums.DocType
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate

/**
 * ViewModel for managing service-related data and user documents.
 *
 * @property auth Firebase authentication instance.
 * @property userDocRepository Repository for user documents.
 * @property serviceRepository Repository for services.
 */
class ServiceViewModel (
    val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val userDocRepository : UserDocumentRepository = UserDocumentRepository(),
    private val serviceRepository : ServiceRepository = ServiceRepository(),
    private val aimaProcessRepository : AimaProcessRepository = AimaProcessRepository()
) : ViewModel() {

    private val _docList = MutableLiveData<List<UserDocument>>()
    val docList: MutableLiveData<List<UserDocument>> = _docList

    private val _hasAllDocumentsApproved = MutableLiveData<Boolean>()
    val hasAllDocumentsApproved: MutableLiveData<Boolean> = _hasAllDocumentsApproved


    /**
     * Loads user documents based on the provided service code.
     *
     * Fetches the user's documents and the required documents for the service.
     * Calls checkIfCompleted to update document status.
     *
     * @param code The code of the service
     */
    fun loadUserDocuments(code: String) {
        val userId = auth.currentUser?.uid.toString()
        var userDocuments: MutableList<UserDocument>? = null
        var requiredDocs: MutableList<String>? = null

        userDocRepository.filterByUser(userId) { documents ->
            if (documents.isNotEmpty()) {
                Log.d("DEBUG", "Found ${documents.size} docs")
                userDocuments = documents
            } else {
                Log.d("DEBUG", "No documents found for user")
                userDocuments = mutableListOf()
            }
            checkIfCompleted(userDocuments, requiredDocs)
        }

        serviceRepository.findServiceById(code) { service ->
            requiredDocs = service?.requiredDocuments ?: mutableListOf()
            checkIfCompleted(userDocuments, requiredDocs)
        }
    }

    /**
     * Checks if all required documents are completed and updates their status.
     *
     * Filters the user's documents to find those that match required document types,
     * updates the status of documents based on expiration, and checks if all documents are approved.
     *
     * @param userDocuments List of user documents.
     * @param requiredDocs List of required document types.
     */
    private fun checkIfCompleted(
        userDocuments: MutableList<UserDocument>?,
        requiredDocs: MutableList<String>?,
    ) {
        if (userDocuments != null && requiredDocs != null) {
            val filteredDocuments = userDocuments.filter { userDocument ->
                userDocument.docType in requiredDocs
            }

            for (doc in filteredDocuments) {
                val expirationDay = LocalDate.parse(doc.expirationDate)
                if (expirationDay.isBefore(LocalDate.now())) {
                    doc.status = DocStatus.EXPIRED.status
                }
            }

            val approvedDocs = filteredDocuments.filter { doc ->
                doc.status == DocStatus.APPROVED.status
            }

            _docList.value = filteredDocuments
            _hasAllDocumentsApproved.value = approvedDocs.size == filteredDocuments.size
        }
    }

    /**
     * Initiates a new process for the specified user and service code.
     *
     * @param userId The unique identifier of the user initiating the process.
     * @param servCode The service code associated with the process to be created.
     * @param onComplete A callback function that is invoked upon completion of the process creation.
     *                   The callback receives a Boolean indicating whether the process creation was successful.
     */
   fun newProcess(userId: String, servCode: String, onComplete: (Boolean) -> Unit){
        val process = AimaProcess(userId, servCode)
        aimaProcessRepository.createProcess(process){ success ->
            onComplete(success)
        }
    }

    fun getAll(callback: (List<Service>) -> Unit) {
        serviceRepository.getAll { services ->
            callback(services)
        }
    }

}
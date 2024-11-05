package com.example.aima_id_app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
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
    val docList: LiveData<List<UserDocument>> = _docList

    private val _serviceList = MutableLiveData<List<Service>>()
    val serviceList: LiveData<List<Service>> = _serviceList

    private val _hasAllDocumentsApproved = MutableLiveData<Boolean>()
    val hasAllDocumentsApproved: LiveData<Boolean> = _hasAllDocumentsApproved


    /**
     * Loads user documents based on the provided service code.
     *
     * Fetches the user's documents and the required documents for the service.
     * Calls checkIfCompleted to update document status.
     *
     * @param code The code of the service
     */
    fun loadUserDocuments() {
        val userId = auth.currentUser?.uid ?: return

        userDocRepository.filterByUser(userId) { documents ->
            if (documents.isNotEmpty()) {
                for (doc in documents) {
                    if (doc.status == DocStatus.APPROVED.status){
                        val expirationDay = LocalDate.parse(doc.expirationDate)
                        if (expirationDay.isBefore(LocalDate.now())) {
                            doc.status = DocStatus.EXPIRED.status
                        }
                    }
                }
                Log.d("DEBUG", "User documents found ${documents.size} docs")
                _docList.value = documents
            } else {
                Log.d("DEBUG", "No documents found for user")
            }
        }

    }


    fun loadServices() {
        serviceRepository.getAll { services ->
            if (services.isNotEmpty()) {
                Log.d("DEBUG", "Services found ${services.size} docs")
                _serviceList.value = services
            } else {
                Log.d("DEBUG", "No documents found for user")
            }
        }
    }

    fun checkDocuments(service: Service) {
        val documents = _docList.value ?: emptyList()

        val filteredDocuments = documents.filter { userDocument ->
            userDocument.docType in service.requiredDocuments
        }

        val approvedDocs = filteredDocuments.filter { doc ->
            doc.status == DocStatus.APPROVED.status
        }

        _hasAllDocumentsApproved.value = approvedDocs.size == filteredDocuments.size
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

    fun getAllServices(callback: (List<Service>) -> Unit) {
        serviceRepository.getAll { services ->
            callback(services)
        }
    }

}
package com.example.aima_id_app.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.util.enums.DocStatus
import com.example.aima_id_app.util.enums.DocType
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate

class ServiceViewModel (
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _docList = MutableLiveData<List<UserDocument>>()
    val docList: MutableLiveData<List<UserDocument>> = _docList

    private val _hasAllDocumentsApproved = MutableLiveData<Boolean>()
    val hasAllDocumentsApproved: MutableLiveData<Boolean> = _hasAllDocumentsApproved


    //-----------------------------

    private val userDocument1 = UserDocument(
        docPath = "path/to/passport.pdf",
        docType = DocType.PASSPORT,
        userId = "55xnD5aqrN2hxo7gGoaY",
        status = DocStatus.SUBMITTED,
        submittedAt = LocalDate.now(),
    )

    private val userDocument2 = UserDocument(
        docPath = "path/to/residence_visa.pdf",
        docType = DocType.RESIDENCE_VISA,
        userId = "55xnD5aqrN2hxo7gGoaY",
        status = DocStatus.APPROVED,
        submittedAt = LocalDate.now().minusDays(10)
    )

    private val userDocument3 = UserDocument(
        docPath = "path/to/visa.pdf",
        docType = DocType.VISA,
        userId = "55xnD5aqrN2hxo7gGoaY",
        status = DocStatus.REJECTED,
        submittedAt = LocalDate.now().minusMonths(1)
    )

    private val userDocument4 = UserDocument(
        docPath = "path/to/health_insurance.pdf",
        docType = DocType.HEALTH_INSURANCE,
        userId = "55xnD5aqrN2hxo7gGoaY",
        status = DocStatus.EXPIRED,
        submittedAt = LocalDate.now().minusYears(2)
    )

    private val userDocument5 = UserDocument(
        docPath = "path/to/address_proof.pdf",
        docType = DocType.ADDRESS_PROOF,
        userId = "55xnD5aqrN2hxo7gGoaY",
        status = DocStatus.APPROVED,
        submittedAt = LocalDate.now().minusDays(5),
    )

    private val userDocuments = mutableListOf<UserDocument>(userDocument1, userDocument2, userDocument3, userDocument4,userDocument5)
    private val requiredDocs = mutableListOf(DocType.ADDRESS_PROOF, DocType.PASSPORT, DocType.VISA)


    //-----------------------------------------------



    fun loadUserDocuments(code: String){
        val userId = auth.currentUser?.uid.toString()

        // Call Repository to User's documents
        val documents = userDocuments

        // Call Repository to requiredDocs
        val requiredDocument = requiredDocs

        val filteredDocuments = userDocuments.filter { userDocument ->
            userDocument.docType in requiredDocs
        }

        for (doc in filteredDocuments){
            if (doc.expirationDate.isBefore(LocalDate.now())){
                doc.status = DocStatus.EXPIRED
            }
        }

        val approvedDocs = filteredDocuments.filter { doc ->
            doc.status == DocStatus.APPROVED
        }

        _docList.value = filteredDocuments
        _hasAllDocumentsApproved.value = approvedDocs.size == filteredDocuments.size

    }
}
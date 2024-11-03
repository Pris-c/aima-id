package com.example.aima_id_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.data.repository.UserDocumentRepository

class SubmittedDocsListViewModel (
    private val userDocumentRepository: UserDocumentRepository = UserDocumentRepository()

) : ViewModel() {

    fun getDocsToAnalyse(onComplete: (MutableList<UserDocument>) -> Unit) {

        userDocumentRepository.filterSubmittedDocs { documents ->
           onComplete(documents)
        }
    }

}
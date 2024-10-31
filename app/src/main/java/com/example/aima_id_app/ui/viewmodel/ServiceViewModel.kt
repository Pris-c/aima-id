package com.example.aima_id_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.util.enums.DocType

class ServiceViewModel : ViewModel() {

    //UserDocumentRepository

    fun loadUserDocuments(userId: String, processCode: String){
        // Retrieve the user's documents

        // find AimaProcess object and recover the required documents
        // create a list of UserDocuments

        // for each DocType in AimaProcess.requiredDoc:
            // if UserDocuments has DocType
                // if expiration date is anterior to today
                    // Update status to EXPIRED
                // Add to list
        // return list of user's documents
    }


    fun isUserDocumentsComplete(requiredDocuments: MutableList<DocType>,
                                userFilteredDocList: MutableList<UserDocument>){
        // forEach requiredDocuments
            // if userFilteredDocList doesn't have DocType or docStatus isn't approved
                // return false

        // return true
    }
}
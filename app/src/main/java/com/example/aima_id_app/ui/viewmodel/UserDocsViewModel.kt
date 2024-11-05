package com.example.aima_id_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.data.repository.DocStorageRepository
import com.example.aima_id_app.data.repository.UserDocumentRepository
import com.google.firebase.auth.FirebaseAuth


/**
 * ViewModel for managing user document interactions.
 * Facilitates the retrieval and updating of documents associated with a user.
 *
 * @property userDocumentRepository The repository responsible for user document interactions.
 * @property storageRepository The repository responsible for storage operations.
 * @property auth The Firebase Authentication instance for managing user sessions.
 */
class UserDocsViewModel(
    private val userDocumentRepository: UserDocumentRepository = UserDocumentRepository(),
    private val storageRepository: DocStorageRepository = DocStorageRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {


    /**
     * Retrieves a mutable list of UserDocument entries associated with the currently
     * authenticated user.
     *
     * @param onComplete A callback function that receives a MutableList of UserDocument entries.
     *                   If no documents are found, an empty list is returned.
     */
    fun getUserDocs(onComplete: (MutableList<UserDocument>) -> Unit) {
        val userId = auth.currentUser?.uid?: return
        userDocumentRepository.filterByUser(userId) { userDocuments ->
            onComplete(userDocuments.ifEmpty { mutableListOf() })
        }
    }

}
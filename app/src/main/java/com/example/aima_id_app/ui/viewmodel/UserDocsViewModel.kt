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
     * Retrieves documents associated with the currently authenticated user.
     *
     * @param onComplete A callback function that receives a map of user documents once the
     * retrieval is complete.
     *
     *    The key is a String identifier for the document, and the value is the
     *    correspondingUserDocument object.
     *
     */
    fun getUserDocs(onComplete: (MutableMap<String, UserDocument>) -> Unit) {
        val userId = auth.currentUser?.uid?: return
        userDocumentRepository.getDocumentsByUser(userId) { userDocuments ->
            onComplete(userDocuments)
        }
    }


}
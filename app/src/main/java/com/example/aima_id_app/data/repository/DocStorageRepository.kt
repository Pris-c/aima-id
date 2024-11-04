package com.example.aima_id_app.data.repository

import android.os.Environment
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

/**
 * Repository class for managing document-related operations in Firebase Storage.
 *
 * Provides methods for saving, retrieving, and deleting documents.
 */
class DocStorageRepository(
    val userDocRepository: UserDocumentRepository = UserDocumentRepository(),
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
) {

    private val storageRefToDelete = FirebaseStorage.getInstance().reference
    private val storageRef = storageRefToDelete.child("userDocuments")

    /**
     * Saves a document to Firebase Storage.
     *
     * @param document The document file to be uploaded.
     * @param onComplete A callback function that returns the storage path if the upload is successful, or null if it fails.
     */
    fun saveFile(document: File, onComplete: (String?) -> Unit) {

        val fileInputStream = FileInputStream(document)
        val baos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var bytesRead: Int

        while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
            baos.write(buffer, 0, bytesRead)
        }
        val fileData = baos.toByteArray()

        val documentRef = storageRef.child("${auth.currentUser?.uid.toString()}_${System.currentTimeMillis()}_${document.name}")
        val uploadTask = documentRef.putBytes(fileData)

        uploadTask
            .addOnSuccessListener {
                onComplete(documentRef.path)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    /**
     * Retrieves a document from Firebase Storage by its path.
     *
     * @param path The storage path of the document.
     * @param onComplete A callback function that returns the retrieved file if successful, or null if it fails.
     */
    fun getFile(path: String, onComplete: (File?) -> Unit) {
        val documentRef = storageRef.child(path)
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        userDocRepository.getFileName(path){ fileName ->

            if (fileName != null){
                val localFile = File(downloadsDir, fileName)
                documentRef.getFile(localFile)
                    .addOnSuccessListener {
                        onComplete(localFile)
                    }
                    .addOnFailureListener { exception ->
                        onComplete(null)
                    }
            } else {
                onComplete(null)
            }
        }
    }

    /**
     * Deletes a document from Firebase Storage by its path.
     *
     * @param path The storage path of the document to delete.
     * @param onComplete A callback function that returns true if the deletion is successful, or false if it fails.
     */
    fun deleteFile(path: String, onComplete: (Boolean) -> Unit) {
        val documentRef = storageRefToDelete.child(path)
        documentRef.delete()
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }

    }
}
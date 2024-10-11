package com.example.aima_id_app.test

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aima_id_app.R
import com.example.aima_id_app.databinding.ActivityFirebaseTestBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class Firebase_test : AppCompatActivity() {

    private val binding by lazy { ActivityFirebaseTestBinding.inflate(layoutInflater) }

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }

    private var selectedUri: Uri? = null
    private var selectedBipmap: Bitmap?= null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_firebase_test)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // User data to test
        val email = "email@email.com"
        val password = "senhadouser"

        // Test to Register
        fun createUserTest(){
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(this , "Regisdo com sucesso", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this , "Falha ao registrar", Toast.LENGTH_SHORT).show()
                }
        }

        // Test to login
        fun loginTest(){
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(this, "Login efetuado: ${it.user?.uid}", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this , "Falha no login", Toast.LENGTH_SHORT).show()
                }
        }

        // Test to Authentication  + Save User on Cloud Firestore
        fun saveAuthenticatedUserOnFirestoreCollection(email: String) {
            val uid = auth.currentUser?.uid

            val user = mapOf(
                "name" to "User 1 da Silva",
                "email" to email,
                "nif" to "999111555",
                "address" to mapOf(
                    "street" to "Rua A",
                    "city" to "Porto"
                )
            )

            db.collection("Users")
                .document(uid.toString())
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Utilizador cadastrado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao cadastrar", Toast.LENGTH_SHORT).show()
                }
        }

        fun createNewUserOnFirebaseAuth(){
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(this , "Auth registrada com sucesso", Toast.LENGTH_SHORT).show()
                    auth.currentUser?.sendEmailVerification()
                    saveAuthenticatedUserOnFirestoreCollection(email)

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Falha ao registrar", Toast.LENGTH_SHORT).show()
                }
        }

        // Test to Storage
        fun uploadImageToFirebase() {
            // Load the image from the drawable resource
            val drawable = getDrawable(R.drawable.nophoto) as BitmapDrawable
            val bitmap = drawable.bitmap

            // Convert the Bitmap into a ByteArray
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageData = baos.toByteArray()

            // Get a reference to Firebase Storage
            val storageRef = storage.reference
            // The Path he must be the one used in Firebase Storage
            val imageRef = storageRef.child("images/nophoto.jpg")

            // Upload the image
            val uploadTask = imageRef.putBytes(imageData)
            uploadTask.addOnFailureListener { exception ->
                // In case of a failure during upload
                Toast.makeText(
                    this,
                    "Upload failed: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("FirebaseStorage", "Upload failed", exception)
            }.addOnSuccessListener {
                // Upload successful
                Toast.makeText(this, "Upload successful!", Toast.LENGTH_SHORT).show()
                Log.d("FirebaseStorage", "Upload successful")
            }
        }

        uploadImageToFirebase()

    }
}
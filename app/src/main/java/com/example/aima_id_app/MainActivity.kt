package com.example.aima_id_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.data.repository.UserDocumentRepository
import com.example.aima_id_app.databinding.ActivityMainBinding
import com.example.aima_id_app.ui.view.AdminActivity
import com.example.aima_id_app.ui.view.LoginActivity
import com.example.aima_id_app.ui.view.StaffActivity
import com.example.aima_id_app.ui.view.UserActivity
import com.example.aima_id_app.util.enums.DocStatus
import com.example.aima_id_app.util.enums.DocType
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

      // TODO : Carregar documentos da Ana

        /*val mockUserDocument = UserDocument(
            "image/path",
            "Passaporte",
            "ZDwOcmkFVhd9ZVoizOrUn7TYiex1",
            "Aprovado",
            LocalDate.of(1,1,1).toString(),
            LocalDate.of(2125,12,30).toString()
        )

        val mockUserDocument1 = UserDocument(
            "image/path",
            "NISS",
            "ZDwOcmkFVhd9ZVoizOrUn7TYiex1",
            "Aprovado",
            LocalDate.of(1,1,1).toString(),
            LocalDate.of(2125,12,30).toString()
        )

        val mockUserDocument2 = UserDocument(
            "image/path",
            "Autoriação de Residência",
            "ZDwOcmkFVhd9ZVoizOrUn7TYiex1",
            "Aprovado",
            LocalDate.of(1,1,1).toString(),
            LocalDate.of(2125,12,30).toString()
        )

        val mockUserDocument3 = UserDocument(
            "image/path",
            "Atestado de Nacionalidade",
            "ZDwOcmkFVhd9ZVoizOrUn7TYiex1",
            "Aprovado",
            LocalDate.of(1,1,1).toString(),
            LocalDate.of(2125,12,30).toString()
        )*/

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        // Create a Handler to start the LoginActivity after 1 second
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)

            /*val repository = UserDocumentRepository()
            val mockDocuments = listOf(mockUserDocument, mockUserDocument1, mockUserDocument2, mockUserDocument3)


            for (mockDocument in mockDocuments) {
                repository.save(mockDocument) { documentId ->
                    if (documentId != null) {

                        Log.d("MainActivity", "Document saved with ID: $documentId")

                    } else {
                        // Save failed
                        Log.e("MainActivity", "Failed to save document")
                    }// Handle success or failure for each mockDocument
                }
            }*/

            // Define transition animations between activities
            overridePendingTransition(R.transition.fade_in, R.transition.fade_out)

            finish()
        }, 1000) // 1 second delay
    }
}
package com.example.aima_id_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aima_id_app.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_main)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val email = "email@email.com"
        val password = "senhadouser"

        // Test to Register
        /*
        auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener {
                Toast.makeText(this , "Regisdo com sucesso", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener{
                Toast.makeText(this , "Falha ao registrar", Toast.LENGTH_SHORT).show()
        }
         */


        // Test to login
        /*
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(this, "Login efetuado: ${it.user?.uid}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(this , "Falha no login", Toast.LENGTH_SHORT).show()
            }
        */


        // Test to Auth  +  Cloud Firestore
        fun saveUser(email: String) {
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

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(this , "Auth registrada com sucesso", Toast.LENGTH_SHORT).show()
                auth.currentUser?.sendEmailVerification()
                saveUser(email)

            }
            .addOnFailureListener {
                Toast.makeText(this, "Falha ao registrar", Toast.LENGTH_SHORT).show()
            }



    }
}
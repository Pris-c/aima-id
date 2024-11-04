package com.example.aima_id_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aima_id_app.databinding.ActivityMainBinding
import com.example.aima_id_app.ui.view.AdminActivity
import com.example.aima_id_app.ui.view.LoginActivity
import com.example.aima_id_app.ui.view.UserActivity
import com.example.aima_id_app.ui.viewmodel.StatusViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    // Initialize the ViewModel using the viewModels delegate
    private val statusViewModel: StatusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Observe the processes LiveData to log or display the data when it updates
        statusViewModel.processes.observe(this) { processes ->
            processes?.let {
                Log.d("MainActivity", "Processes retrieved: $it")
                Toast.makeText(this, "Processes Loaded: ${it.size}", Toast.LENGTH_SHORT).show()
            } ?: run {
                Log.d("MainActivity", "No processes found for this user.")
                Toast.makeText(this, "No processes found for this user.", Toast.LENGTH_SHORT).show()
            }
        }

        // Call the function to load processes by user ID
        statusViewModel.getProcessesByUserId()// Observe the processes LiveData to log or display the data when it updates
        statusViewModel.processes.observe(this) { processes ->
            processes?.let {
                Log.d("Aoba", "Processes retrieved: $it")
                Toast.makeText(this, "Processes Loaded: ${it.size}", Toast.LENGTH_SHORT).show()
            } ?: run {
                Log.d("Aoba", "No processes found for this user.")
                Toast.makeText(this, "No processes found for this user.", Toast.LENGTH_SHORT).show()
            }
        }

        // Call the function to load processes by user ID
        statusViewModel.getProcessesByUserId()
        // Create a Handler to start the LoginActivity after 1 second

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)

            // Define transition animations between activities
            overridePendingTransition(R.transition.fade_in, R.transition.fade_out)

            finish()
        }, 1000) // 1 second delay
    }
}
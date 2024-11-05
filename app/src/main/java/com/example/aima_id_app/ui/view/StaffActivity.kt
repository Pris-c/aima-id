package com.example.aima_id_app.ui.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.aima_id_app.R
import com.google.firebase.auth.FirebaseAuth

class StaffActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_staff)

        findViewById<View>(R.id.cardView1).setOnClickListener {
            loadFragment(DocsAnalysisStaffFragment())
        }

        findViewById<View>(R.id.cardView2).setOnClickListener {
            loadFragment(AppointmentsFragment())
        }

        val user = FirebaseAuth.getInstance().currentUser
        val userName = user?.displayName
        val textViewNameUser = findViewById<TextView>(R.id.textViewNameStaff)
        textViewNameUser.text = if (userName != null) "Olá $userName" else "Olá, Bem-vindo!"


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Loads a fragment into the fragment container with custom slide animations.
     *
     * Replaces the current fragment in the container with the provided fragment,
     * adding it to the back stack for navigation. Uses slide-in/slide-out animations
     * for a smooth transition.
     *
     * @param fragment The fragment to be loaded.
     */
    fun loadFragment(fragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null || currentFragment::class != fragment::class) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.animator.slide_in_up,
                    R.animator.slide_out_down,
                    R.animator.slide_in_up,
                    R.animator.slide_out_down
                )
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        } else{
            supportFragmentManager.popBackStack()
        }
    }
}
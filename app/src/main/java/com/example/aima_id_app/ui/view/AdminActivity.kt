package com.example.aima_id_app.ui.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.aima_id_app.R
import com.example.aima_id_app.ui.view.RegisterUnitAdminFragment
import com.example.aima_id_app.ui.viewmodel.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)


        findViewById<View>(R.id.card_register_unit).setOnClickListener {
            loadFragment(RegisterUnitAdminFragment())
        }

        findViewById<View>(R.id.card_register_staff).setOnClickListener {
            loadFragment(RegisterStaffAdminFragment())
        }

        var userName = " Bem-vindo!"
        var userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null){
            userId = ""
        }

        HomeViewModel().getUserById(userId) { user ->
            if (user != null) {
                userName = user.name.split(" ")[0]
            }


            val textViewNameUser = findViewById<TextView>(R.id.textViewNameAdmin)
            textViewNameUser.text = "Olá, ${userName}"

            val bottomNavigationView =
                findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigationView.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        supportFragmentManager.popBackStack(
                            null,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                        true
                    }

                    R.id.nav_profile -> {
                        loadFragment(ProfileFragment())
                        true
                    }

                    R.id.nav_calendar -> {
                        loadFragment(SchedulingUserFragment())
                        true
                    }

                    R.id.nav_notifications -> {
                        loadFragment(NotificationsFragment())
                        true
                    }

                    R.id.nav_contact -> {
                        loadFragment(ContactUserFragment())
                        true
                    }

                    else -> false
                }
            }

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
                )
                insets
            }
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
    private fun loadFragment(fragment: Fragment) {
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
    }

}

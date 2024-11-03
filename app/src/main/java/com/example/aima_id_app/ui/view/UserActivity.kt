package com.example.aima_id_app.ui.view

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.aima_id_app.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user)

        findViewById<View>(R.id.card_service).setOnClickListener {
            loadFragment(RequestResidencyFragment())
        }

        findViewById<View>(R.id.card_upload).setOnClickListener {
            loadFragment(UploadDocsFragment())
        }

        findViewById<View>(R.id.card_scheduling).setOnClickListener {
            loadFragment(SchedulingFragment())
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                R.id.nav_calendar -> {
                    loadFragment(SchedulingFragment())
                    true
                }
                R.id.nav_notifications -> {
                    loadFragment(SchedulingFragment())
                    true
                }
                R.id.nav_contact -> {
                    loadFragment(SchedulingFragment())
                    true
                }
                else -> false
            }
        }


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
    private fun loadFragment(fragment: Fragment) {
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
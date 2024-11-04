package com.example.aima_id_app.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aima_id_app.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.card_update_profile).setOnClickListener {
            loadFragment(UpdateProfileFragment())
        }

        view.findViewById<View>(R.id.card_docs).setOnClickListener {
            loadFragment(DocsUserFragment())
        }

        view.findViewById<View>(R.id.card_myID).setOnClickListener {
            loadFragment(DigitalIDFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        
        parentFragmentManager.beginTransaction()
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
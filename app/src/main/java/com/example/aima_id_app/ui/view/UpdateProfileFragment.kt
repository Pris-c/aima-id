package com.example.aima_id_app.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aima_id_app.R

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateProfileFragment : Fragment() {

    /**
     * Inflates the layout for the UpdateProfileFragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_profile, container, false)
    }

}
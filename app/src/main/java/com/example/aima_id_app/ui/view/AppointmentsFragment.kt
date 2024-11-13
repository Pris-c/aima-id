package com.example.aima_id_app.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aima_id_app.R

class AppointmentsFragment : Fragment() {

    /**
     * Inflates the layout for the AppointmentsFragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_appointments, container, false)
    }
}
package com.example.aima_id_app.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aima_id_app.R
import com.example.aima_id_app.ui.viewmodel.CalendarViewModel
import java.time.YearMonth

class SchedulingUserFragment : Fragment() {

    /**
     * Inflates the layout and initializes views for SchedulingUserFragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        return inflater.inflate(R.layout.fragment_user_scheduling, container, false)
    }
}
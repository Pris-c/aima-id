package com.example.aima_id_app.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aima_id_app.R
import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.ui.viewmodel.DocumentAnalysisViewModel
import com.example.aima_id_app.ui.viewmodel.SubmittedDocsListViewModel
import com.example.aima_id_app.util.adapter.CardDocAnalyse

class DocsAnalysisStaffFragment : Fragment() {

    private lateinit var recyclerViewFiles: RecyclerView
    private val submittedDocsListViewModel = SubmittedDocsListViewModel()
    private val documentAnalysisViewModel = DocumentAnalysisViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_staff_user_analysis, container, false)
        recyclerViewFiles = view.findViewById(R.id.recyclerViewFiles)
        recyclerViewFiles.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        submittedDocsListViewModel.getDocsToAnalyse { documents ->
            val adapter = CardDocAnalyse(requireContext(), documents, documentAnalysisViewModel)
            recyclerViewFiles.adapter = adapter
        }
    }
}
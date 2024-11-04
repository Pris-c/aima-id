package com.example.aima_id_app.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aima_id_app.R
import com.example.aima_id_app.ui.adapter.UserDocsAdapter
import com.example.aima_id_app.ui.viewmodel.ServiceViewModel

class DocsUserFragment : Fragment() {

    private val serviceViewModel = ServiceViewModel()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_docs, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewFiles)
        recyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Carregar os documentos do usuário
        serviceViewModel.loadUserDocuments("")

        // Observar as mudanças na lista de documentos
        serviceViewModel.docList.observe(viewLifecycleOwner) { documents ->
            val adapter = UserDocsAdapter(documents)
            recyclerView.adapter = adapter
        }

        view.findViewById<View>(R.id.updateDocsButton).setOnClickListener {
            loadFragment(UploadDocsFragment())
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
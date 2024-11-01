package com.example.aima_id_app.ui.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aima_id_app.R
import com.example.aima_id_app.data.repository.ServiceRepository
import com.example.aima_id_app.data.repository.UserRepository
import com.example.aima_id_app.ui.adapter.FileInputAdapter
import com.example.aima_id_app.util.enums.DocType
import com.google.firebase.auth.FirebaseAuth

class RequestResidencyFragment : Fragment() {

    private lateinit var recyclerViewFiles: RecyclerView
    private lateinit var spinner: Spinner
    private val serviceRepository = ServiceRepository()
    private var selectedFiles: MutableMap<DocType, Uri?> = mutableMapOf()
    private val userRepository = UserRepository()
    private val firebaseAuth = FirebaseAuth.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_request_residency, container, false)
        recyclerViewFiles = view.findViewById(R.id.recyclerViewFiles)
        recyclerViewFiles.layoutManager = LinearLayoutManager(context)
        spinner = view.findViewById(R.id.nameStaff_input)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serviceRepository.getAll { services ->
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, services.map { it.name })
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = spinnerAdapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedService = services[position]
                    val fileInputs = selectedService.requiredDocuments

                    // Buscar os documentos do usuário no Firebase
                    val currentUser = firebaseAuth.currentUser
                    if (currentUser != null) {
                        userRepository.getUserDocuments(currentUser.uid) { userDocuments ->
                            // Criar o adapter com a lista de documentos do usuário
                            val adapter = FileInputAdapter(requireContext(), fileInputs, userDocuments)
                            recyclerViewFiles.adapter = adapter
                        }
                    } else {
                        // Lidar com o caso em que o usuário não está logado
                        // Exibir uma mensagem de erro ou redirecionar para a tela de login
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // ...
                }
            }
        }
    }
}
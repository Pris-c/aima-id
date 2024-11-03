package com.example.aima_id_app.ui.view

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
import com.example.aima_id_app.ui.adapter.FileInputAdapter
import com.example.aima_id_app.ui.viewmodel.ServiceViewModel
import com.google.android.material.textfield.TextInputEditText

class RequestServiceFragment : Fragment() {

    private lateinit var recyclerViewFiles: RecyclerView
    private lateinit var spinner: Spinner
    private val serviceViewModel = ServiceViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_request_service, container, false)
        recyclerViewFiles = view.findViewById(R.id.recyclerViewFiles)
        recyclerViewFiles.layoutManager = LinearLayoutManager(context)
        spinner = view.findViewById(R.id.nameStaff_input)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textAreaInput = view.findViewById<TextInputEditText>(R.id.textArea_input)

        val serviceDescriptions = mapOf(
            "Pedido de Autorização de Residencia: Trabalho" to getString(R.string.DescriptionRequestResidencyWorkService),
            "Pedido de Autorização de Residencia: Estudo" to getString(R.string.DescriptionRequestResidencyStudyService),
            "Extensão de visto" to getString(R.string.DescriptionRequestEqualityService),
            "Estatuto de Igualdade de Direitos e Deveres Sociais" to getString(R.string.DescriptionRequestVisaExtensionService),
            "Renovação de Autorização de Residência" to getString(R.string.DescriptionRequestRenewalResidencyService)
        )

        serviceViewModel.getAll { services ->
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, services.map { it.name })
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = spinnerAdapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedService = services[position]
                    val fileInputs = selectedService.requiredDocuments

                    serviceViewModel.loadUserDocuments(selectedService.name)

                    serviceViewModel.docList.observe(viewLifecycleOwner) { documents ->
                        val adapter = FileInputAdapter(requireContext(), selectedService.requiredDocuments, documents)
                        recyclerViewFiles.adapter = adapter
                    }

                    val serviceDescription = serviceDescriptions[selectedService.name] ?: ""

                    textAreaInput.setText(serviceDescription)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // ...
                }
            }
        }
    }
}
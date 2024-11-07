package com.example.aima_id_app.ui.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aima_id_app.R
import com.example.aima_id_app.ui.adapter.FileInputAdapter
import com.example.aima_id_app.ui.viewmodel.ServiceViewModel
import com.example.aima_id_app.util.enums.DocType
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class RequestServiceFragment : Fragment() {

    private lateinit var recyclerViewFiles: RecyclerView
    private lateinit var spinner: Spinner
    private lateinit var registerServiceButton: Button
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

        serviceViewModel.hasAllDocumentsApproved.observe(viewLifecycleOwner) { hasAllApproved ->
            registerServiceButton.isEnabled = hasAllApproved

            if (hasAllApproved) {
                registerServiceButton.alpha = 1.0f  // Deixa o botão visível
            } else {
                registerServiceButton.alpha = 0.5f  // Torna o botão visível, mas desabilitado visualmente
            }
        }

        val serviceDescriptions = mapOf(
            "Pedido de Autorização de Residencia: Trabalho" to getString(R.string.DescriptionRequestResidencyWorkService),
            "Pedido de Autorização de Residencia: Estudo" to getString(R.string.DescriptionRequestResidencyStudyService),
            "Extensão de visto" to getString(R.string.DescriptionRequestEqualityService),
            "Estatuto de Igualdade de Direitos e Deveres Sociais" to getString(R.string.DescriptionRequestVisaExtensionService),
            "Renovação de Autorização de Residência" to getString(R.string.DescriptionRequestRenewalResidencyService)
        )
        val textAreaInput = view.findViewById<TextInputEditText>(R.id.textArea_input)


        // Observa o LiveData serviceList para carregar os serviços
        serviceViewModel.serviceList.observe(viewLifecycleOwner) { services ->

            // Verifica se a lista de serviços foi carregada corretamente
            if (services.isNotEmpty()) {

                // Preenche o Spinner com os serviços
                val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, services.map { it.name })
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = spinnerAdapter

                // Configura o listener para quando um item for selecionado
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedService = services[position]
                        val requiredDocs = selectedService.requiredDocuments

                        // Acessa o docList atual e chama checkDocuments para verificar os documentos
                        serviceViewModel.checkDocuments(selectedService)

                        // A lista de documentos deve ser observada para atualizar a UI
                        val userDocuments = serviceViewModel.docList.value ?: emptyList()
                        val docTypeList: List<DocType> = requiredDocs.mapNotNull { DocType.fromType(it) }

                        val adapter = FileInputAdapter(requireContext(), docTypeList, userDocuments)
                        recyclerViewFiles.adapter = adapter

                        // Atualiza a descrição do serviço no campo de texto
                        val serviceDescription = serviceDescriptions[selectedService.name] ?: ""
                        textAreaInput.setText(serviceDescription)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Comportamento quando nenhum item é selecionado (pode ser vazio)
                    }
                }
            }
        }

        serviceViewModel.docList.observe(viewLifecycleOwner) { documents ->
            // Atualiza a lista de documentos do usuário no adapter
            val adapter = recyclerViewFiles.adapter as? FileInputAdapter
            adapter?.userDocuments = documents
            adapter?.notifyDataSetChanged()
        }


        serviceViewModel.loadUserDocuments()
        serviceViewModel.loadServices()

        registerServiceButton = view.findViewById(R.id.registerServiceButton)

        registerServiceButton.setOnClickListener {
            val selectedServicePosition = spinner.selectedItemPosition

            // Carrega os serviços
            serviceViewModel.getAllServices { services ->
                val selectedService = services[selectedServicePosition]
                val selectedServiceCode = selectedService.code

                // Agora, verifica se todos os documentos foram aprovados
                serviceViewModel.hasAllDocumentsApproved.observe(viewLifecycleOwner) { hasAllApproved ->
                    if (hasAllApproved) {
                        val userId = serviceViewModel.auth.currentUser?.uid.toString()

                        // Inicia um novo processo com o serviço selecionado
                        serviceViewModel.newProcess(userId, selectedServiceCode) { success ->
                            if (success) {
                                Snackbar.make(requireView(), "Serviço registrado com sucesso.", Snackbar.LENGTH_SHORT).show()
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        requireActivity().supportFragmentManager.popBackStack()
                                    }, 1000) // 1 segundo de atraso
                            } else {
                                Snackbar.make(requireView(), "Falha ao registrar o serviço.", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Snackbar.make(requireView(), "Por favor, aprove todos os documentos antes de prosseguir.", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
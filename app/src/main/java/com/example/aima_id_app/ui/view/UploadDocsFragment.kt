package com.example.aima_id_app.ui.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.aima_id_app.R
import com.example.aima_id_app.util.enums.DocType
import com.google.android.material.textfield.TextInputEditText
import android.provider.OpenableColumns


class UploadDocsFragment : Fragment() {

    private lateinit var spinner: Spinner
    private lateinit var inputFileEditText: TextInputEditText
    private var selectedFileUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload_docs, container, false)
        spinner = view.findViewById(R.id.nameStaff_input)
        inputFileEditText = view.findViewById(R.id.inputFileEditText)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val docTypes = DocType.values().toList()
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, docTypes.map { getTitleForDocType(it) })
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter


        inputFileEditText.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "*/*" // all types files
            }
            startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
        }

    }

    private fun getTitleForDocType(docType: DocType): String {
        return when (docType) {
            DocType.PASSPORT -> "Passaporte"
            DocType.VISA -> "Visto"
            DocType.NIF -> "NIF"
            DocType.NISS -> "NISS"
            DocType.PROOF_INCOME -> "Prova de renda"
            DocType.CITIZENSHIP_ID -> "Certidão nacionalidade"
            DocType.ADDRESS_PROOF -> "Atestado de morada"
            DocType.HEALTH_INSURANCE -> "Seguro Saúde"
            DocType.CRIMINAL_RECORD -> "Atestado criminalidade"
            DocType.EMPLOYMENT_CONTRACT -> "Contrato de trabalho"
            DocType.SCHOOL_REGISTRATION -> "Matrícula escolar"
            DocType.RESIDENCE_VISA -> "Visto de residência"
            DocType.RESIDENT_PERMIT -> "Autorização de residência"
            else -> "Selecione um documento/arquivo" // default
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            uri?.let {
                inputFileEditText.setText(getFileNameFromUri(it))
                selectedFileUri = it
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = ""
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    fileName = it.getString(displayNameIndex)
                }
            }
        }
        return fileName
    }

    companion object {
        const val REQUEST_CODE_PICK_FILE = 1001
    }
}
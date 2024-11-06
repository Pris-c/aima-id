package com.example.aima_id_app.ui.view

import android.app.Activity
import android.content.Context
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
import androidx.lifecycle.ViewModelProvider
import com.example.aima_id_app.ui.viewmodel.UploadDocsViewModel
import com.example.aima_id_app.util.enums.DocStatus
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException


class UploadDocsFragment : Fragment() {

    private lateinit var spinner: Spinner
    private lateinit var inputFileEditText: TextInputEditText
    private var selectedFileUri: Uri? = null
    private lateinit var viewModel: UploadDocsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload_docs, container, false)
        spinner = view.findViewById(R.id.nameStaff_input)
        inputFileEditText = view.findViewById(R.id.inputFileEditText)
        viewModel = ViewModelProvider(this).get(UploadDocsViewModel::class.java)
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

        val uploadButton = view.findViewById<MaterialButton>(R.id.uploadButton)
        uploadButton.setOnClickListener {
            val selectedDocType = DocType.values()[spinner.selectedItemPosition]
            val fileUri = selectedFileUri
            val status = DocStatus.SUBMITTED.status

            if (fileUri != null) {
                try {
                    val file = getFileFromUri(requireContext(), fileUri)
                    if (file != null) {
                        viewModel.saveOrUpdateDoc(file, selectedDocType, status) { success ->
                            if (success) {
                                Snackbar.make(view, "Documento enviado com sucesso!", Snackbar.LENGTH_SHORT).show()
                                file.delete()
                            } else {
                                viewModel.documentErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                                    errorMessage?.let {
                                        Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    } else {
                        Snackbar.make(view, "Erro ao converter o arquivo.", Snackbar.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    Snackbar.make(view, "Erro ao acessar o arquivo.", Snackbar.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            } else {
                Snackbar.make(view, "Selecione um arquivo!", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val fileName = getFileNameFromUri(uri)

        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("temp", fileName?.substringAfterLast('.'), context.cacheDir)
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun getTitleForDocType(docType: DocType): String {
        return when (docType) {
            DocType.PASSPORT -> "Passaporte"
            DocType.VISA -> "Visto"
            DocType.NIF -> "NIF"
            DocType.NISS -> "NISS"
            DocType.PROOF_INCOME -> "Comprovativo de Meios de Subsistência"
            DocType.CITIZENSHIP_ID -> "Atestado de Nacionalidade"
            DocType.ADDRESS_PROOF -> "Comprovativo de morada"
            DocType.HEALTH_INSURANCE -> "Seguro Saúde"
            DocType.CRIMINAL_RECORD -> "Registro Criminal"
            DocType.EMPLOYMENT_CONTRACT -> "Contrato de Trabalho"
            DocType.SCHOOL_REGISTRATION -> "Matricula Escolar"
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
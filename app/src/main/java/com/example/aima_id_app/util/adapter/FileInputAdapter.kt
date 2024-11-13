package com.example.aima_id_app.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aima_id_app.R
import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.util.enums.DocStatus
import com.example.aima_id_app.util.enums.DocType

class FileInputAdapter(
    private val context: Context,
    private val fileInputs: List<DocType>,
    var userDocuments: List<UserDocument>

) : RecyclerView.Adapter<FileInputAdapter.ViewHolder>() {

    companion object {
        const val REQUEST_CODE_PICK_FILE = 155
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_file_input, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val docType = fileInputs[position]
        Log.d("FileInputAdapter", "docType: $docType")
        holder.iconImageView.setImageResource(getIconForDocType(docType))
        holder.titleTextView.text = getTitleForDocType(docType)

        userDocuments.forEach {
            Log.d("FileInputAdapter", "it.docType: ${it.docType}")
        }

        val userDocument = userDocuments.find { it.docType == docType.doc }
        Log.d("FileInputAdapter", "userDocument: $userDocument")

        holder.descriptionTextView.text = when (userDocument?.status) {
            DocStatus.SUBMITTED.status -> "Submetido"
            DocStatus.APPROVED.status -> "Aprovado"
            DocStatus.REJECTED.status -> "Rejeitado"
            DocStatus.EXPIRED.status -> "Expirado"
            else -> {
                Log.d("FileInputAdapter", "Unknown status: ${userDocument?.status}") // Log para status desconhecido
                "Não enviado"
            }
        }
    }

    override fun getItemCount(): Int {
        return fileInputs.size
    }

    private fun getIconForDocType(docType: DocType): Int {
        return when (docType) {
            DocType.PASSPORT -> R.drawable.ic_passport
            DocType.VISA -> R.drawable.ic_passport
            DocType.NIF -> R.drawable.id_card_identity
            DocType.NISS -> R.drawable.id_card_identity
            DocType.PROOF_INCOME -> R.drawable.ic_money
            DocType.CITIZENSHIP_ID -> R.drawable.ic_cardid
            DocType.ADDRESS_PROOF -> R.drawable.ic_address
            DocType.HEALTH_INSURANCE -> R.drawable.ic_healthcare
            DocType.CRIMINAL_RECORD -> R.drawable.ic_criminal
            DocType.EMPLOYMENT_CONTRACT -> R.drawable.ic_contract
            DocType.SCHOOL_REGISTRATION -> R.drawable.ic_school
            DocType.RESIDENT_PERMIT -> R.drawable.ic_cardid
            else -> R.drawable.ic_to_do_list
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
            else -> "Documento"
        }
    }

    private fun getDescriptionForDocStatus(docStatus: DocStatus): String {
        return when (docStatus) {
            DocStatus.SUBMITTED -> "Enviado"
            DocStatus.APPROVED -> "Aprovado"
            DocStatus.REJECTED -> "Rejeitado"
            DocStatus.EXPIRED -> "Expirado"
            null -> "Pendente"
            else -> "Não enviado"
        }
    }
}
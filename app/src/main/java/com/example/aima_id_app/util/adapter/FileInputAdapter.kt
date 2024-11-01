package com.example.aima_id_app.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aima_id_app.R
import com.example.aima_id_app.util.enums.DocType

class FileInputAdapter(
    private val context: Context,
    private val fileInputs: List<DocType>,
    private val onFileSelected: (DocType, Uri?) -> Unit
) : RecyclerView.Adapter<FileInputAdapter.ViewHolder>() {

    companion object {
        const val REQUEST_CODE_PICK_FILE = 155
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val docType = fileInputs[position]

                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        type = when (docType) {
                            DocType.PASSPORT -> "application/pdf/*"
                            DocType.VISA -> "application/pdf/*"
                            DocType.NIF -> "application/pdf/*"
                            DocType.NISS -> "application/pdf/*"
                            DocType.PROOF_INCOME -> "application/pdf/*"
                            DocType.CITIZENSHIP_ID -> "application/pdf/*"
                            DocType.ADDRESS_PROOF -> "application/pdf/*"
                            DocType.HEALTH_INSURANCE -> "application/pdf/*"
                            DocType.CRIMINAL_RECORD -> "application/pdf/*"
                            DocType.EMPLOYMENT_CONTRACT -> "application/pdf/*"
                            DocType.SCHOOL_REGISTRATION -> "application/pdf/*"
                            DocType.RESIDENCE_VISA -> "application/pdf/*"
                            DocType.RESIDENT_PERMIT -> "application/pdf/*"
                            else -> "*/*"
                        }
                    }

                    (context as Activity).startActivityForResult(
                        intent,
                        REQUEST_CODE_PICK_FILE
                    )

                    itemView.tag = docType
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_file_input, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val docType = fileInputs[position]
        holder.iconImageView.setImageResource(getIconForDocType(docType))
        holder.titleTextView.text = getTitleForDocType(docType)
        holder.descriptionTextView.text = getDescriptionForDocType(docType)
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
            DocType.RESIDENCE_VISA -> R.drawable.ic_passport
            DocType.RESIDENT_PERMIT -> R.drawable.ic_cardid
            else -> R.drawable.ic_to_do_list // default
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
            else -> "Documento" // default
        }
    }

    private fun getDescriptionForDocType(docType: DocType): String {
        return when (docType) {
            DocType.PASSPORT -> "Clique para carregar seu arquivo"
            // ... outros tipos de documentos ...
            else -> "Clique para carregar seu arquivo" // Default
        }
    }
}
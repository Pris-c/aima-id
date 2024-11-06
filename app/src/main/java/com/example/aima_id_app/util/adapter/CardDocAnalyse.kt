package com.example.aima_id_app.util.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.aima_id_app.R
import com.example.aima_id_app.data.model.db_model.UserDocument
import com.example.aima_id_app.ui.viewmodel.DocumentAnalysisViewModel
import com.example.aima_id_app.util.enums.DocType
import com.google.android.material.snackbar.Snackbar
import java.io.File

class CardDocAnalyse(
    private val context: Context,
    private var documents: MutableMap<String, UserDocument>,
    private val viewModel: DocumentAnalysisViewModel,
) : RecyclerView.Adapter<CardDocAnalyse.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val approveButton: Button = itemView.findViewById(R.id.approveButton)
        val reproveButton: Button = itemView.findViewById(R.id.reproveButton)
        val downloadButton: FrameLayout = itemView.findViewById(R.id.downloadButton)

        val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        val userNifTextView: TextView = itemView.findViewById(R.id.userNifTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_doc_analyse, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val document = documents.values.toList()[position]
        val docId = documents.keys.toList()[position]

        val docType = DocType.fromType(document.docType) ?: return

        holder.iconImageView.setImageResource(R.drawable.ic_download)
        holder.titleTextView.text = docType.doc

        holder.userNameTextView.text = document.userId
        holder.userNifTextView.text = document.submittedAt

        holder.approveButton.setOnClickListener {
            viewModel.approveDoc(docId)
            documents.remove(docId)
            notifyItemRemoved(position)
        }

        holder.reproveButton.setOnClickListener {
            viewModel.rejectDoc(docId)
            documents.remove(docId)
            notifyItemRemoved(position)
        }

        holder.downloadButton.setOnClickListener {
            val document = documents.values.toList()[position]
            viewModel.downloadDocument(document) { filePath ->
                if (filePath != null) {
                    val file = File(filePath)
                    val uri = FileProvider.getUriForFile(context, "${context.applicationContext.packageName}.fileprovider", file)

                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "application/pdf")
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.startActivity(intent)
                } else {
                    // Exiba uma mensagem de erro ao usuário
                    Snackbar.make(holder.itemView, "Erro ao baixar o documento", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return documents.size
    }

}
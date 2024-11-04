package com.example.aima_id_app.data.model.db_model

import com.example.aima_id_app.util.enums.DocType

class Service(
    val name: String = "",
    val hasPresencialAttendance: Boolean = false,
    val requiredDocuments: MutableList<String> = mutableListOf<String>()
) {}
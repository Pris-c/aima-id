package com.example.aima_id_app.data.model.db_model

class Service(
    val name: String = "",
    val hasPresencialAttendance: Boolean = false,
    val requiredDocuments: MutableList<String> = mutableListOf<String>()
)
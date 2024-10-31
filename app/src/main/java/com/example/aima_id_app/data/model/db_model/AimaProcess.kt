package com.example.aima_id_app.data.model.db_model

import com.example.aima_id_app.util.enums.ProcessStatus
import java.time.LocalDate

data class AimaProcess (
    val userId: String = "",
    val serviceCode: String = "",
    var status: ProcessStatus = ProcessStatus.IN_ANALYSIS,
    val createdAt: String = LocalDate.now().toString(),
    var updatedAt: String = LocalDate.now().toString()
){
}
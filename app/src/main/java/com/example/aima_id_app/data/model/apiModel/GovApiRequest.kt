package com.example.aima_id_app.data.model.apiModel

import com.google.gson.annotations.SerializedName

data class GovApiRequest(
    @SerializedName("name") val name: String?,
    @SerializedName("nif") val nif: String?,
    @SerializedName("birthdate") val birthdate: String?,


    //val name: String,
    //val nif: String,
    //val birthdate: String
)
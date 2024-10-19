package com.example.aima_id_app.data.service

import com.example.aima_id_app.data.model.apiModel.AdviceResponse
import com.example.aima_id_app.data.model.apiModel.GovApiRequest
import com.example.aima_id_app.data.model.apiModel.GovApiResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface GovApiInterface {

    @Headers("Content-Type: application/json")
    @POST("/api/citizen/verify")
    fun verifyCitizen(@Body request: GovApiRequest): Call<GovApiResponse>

   /* @GET("/advice")
    fun getAdvice(): Call<AdviceResponse>*/

}
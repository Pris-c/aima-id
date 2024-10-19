package com.example.aima_id_app.data.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

     private const val BASE_URL = "http://10.0.2.2:8080/"
    //private const val BASE_URL = "  https://api.adviceslip.com"


    val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
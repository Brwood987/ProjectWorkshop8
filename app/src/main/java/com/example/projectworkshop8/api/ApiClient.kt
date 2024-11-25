//Written by Ben Wood
package com.example.projectworkshop8.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//connect to the android emulator(10.0.2.2 and then 3000 is whatever port the api runs on)
object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: TravelExpertsApi by lazy {
        retrofit.create(TravelExpertsApi::class.java)
    }
}

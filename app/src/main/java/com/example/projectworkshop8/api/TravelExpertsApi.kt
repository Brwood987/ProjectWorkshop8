package com.example.projectworkshop8.api

import com.example.projectworkshop8.models.Product
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TravelExpertsApi {
    @GET("products")
    fun getAllProducts(): Call<List<Product>>

    @POST("products")
    fun createProduct(@Body product: Product): Call<Void>

    @PUT("products/{id}")
    fun updateProduct(@Path("id") id: Int, @Body product: Product): Call<Void>

    @DELETE("products/{id}")
    fun deleteProduct(@Path("id") id: Int): Call<Void>
}

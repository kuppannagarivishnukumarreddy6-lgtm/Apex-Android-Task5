package com.example.studentregistration

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class User(
    val id: Int = 0,
    val name: String,
    val email: String
)

interface ApiService {

    // GET Method
    @GET("users/1")
    fun getUser(): Call<User>

    // POST Method
    @POST("users")
    fun createUser(
        @Body user: User
    ): Call<User>

    // PUT Method
    @PUT("users/{id}")
    fun updateUser(
        @Path("id") id: Int,
        @Body user: User
    ): Call<User>

    // DELETE Method
    @DELETE("users/{id}")
    fun deleteUser(
        @Path("id") id: Int
    ): Call<Void>
}
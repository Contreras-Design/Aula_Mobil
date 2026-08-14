package com.itespf.aulamovil.data.network

import com.itespf.aulamovil.data.model.GradesResponse
import com.itespf.aulamovil.data.model.LoginRequest
import com.itespf.aulamovil.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<Unit>

    @GET("api/v1/grades")
    suspend fun getGrades(): Response<GradesResponse>
}

package com.itsm.caremycar.api

import com.itsm.caremycar.session.LoginRequest
import com.itsm.caremycar.session.LoginResponse
import com.itsm.caremycar.session.RegisterRequest
import com.itsm.caremycar.session.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}
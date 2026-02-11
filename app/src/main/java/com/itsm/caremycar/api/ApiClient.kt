package com.itsm.caremycar.api

import com.google.firebase.appdistribution.gradle.ApiService

object ApiClient {
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }
}
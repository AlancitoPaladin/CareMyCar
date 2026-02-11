package com.itsm.caremycar.session

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String? = null
)
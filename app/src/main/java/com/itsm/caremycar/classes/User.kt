package com.itsm.caremycar.classes

data class User (
    val id: String,
    val email: String,
    val role: String,
    val name: String? = null
)
package com.itsm.caremycar.classes

import com.google.gson.annotations.SerializedName

data class UserDto (
    @SerializedName("id")
    val id: String,
    val email: String,
    val role: String? = null,
    val name: String? = null
)

fun UserDto.toUser() = User(
    id = id,
    email = email,
    role = role ?: "user",
    name = name
)
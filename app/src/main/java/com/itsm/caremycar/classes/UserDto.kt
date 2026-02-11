package com.itsm.caremycar.classes

import com.google.gson.annotations.SerializedName

data class UserDto (
    @SerializedName("_id")
    val id: String,
    val email: String,
    val role: String,
    val name: String? = null
)

fun UserDto.toUser() = User(
    id = id,
    email = email,
    role = role,
    name = name
)
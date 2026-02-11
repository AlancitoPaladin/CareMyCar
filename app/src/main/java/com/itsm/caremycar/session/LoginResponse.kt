package com.itsm.caremycar.session

import com.google.gson.annotations.SerializedName
import com.itsm.caremycar.classes.UserDto

data class LoginResponse (
    @SerializedName("access_token")
    val accessToken: String,
    val user: UserDto
)
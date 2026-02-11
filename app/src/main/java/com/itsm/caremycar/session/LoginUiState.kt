package com.itsm.caremycar.session

import com.itsm.caremycar.classes.User

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val user: User? = null
)
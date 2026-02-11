package com.itsm.caremycar.session

import com.itsm.caremycar.classes.User

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val error: String? = null,
    val user: User? = null
)
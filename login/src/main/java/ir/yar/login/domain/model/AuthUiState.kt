package ir.yar.login.domain.model

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val data: ir.yar.login.domain.model.AuthResult? = null
)

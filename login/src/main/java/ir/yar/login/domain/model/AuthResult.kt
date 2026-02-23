package ir.yar.login.domain.model

data class AuthResult(
    val token: String,
    val user: ir.yar.login.domain.model.User
)
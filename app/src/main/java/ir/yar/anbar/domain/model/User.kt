package ir.yar.anbar.domain.model

import java.time.LocalDateTime

data class User(
    val id: Long,
    val phoneNumber: String,
    val fullName: String,
    val profileImageUrl: String?,
    val role: String,
    val enabled: Boolean,
    val createdAt: LocalDateTime?,
    val lastLogin: LocalDateTime?
) {
    val isAdmin: Boolean
        get() = role.equals("ADMIN", ignoreCase = true)
}

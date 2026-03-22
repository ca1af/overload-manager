package com.calaf.overloadmanager.auth.domain.port.`in`

data class RegisterCommand(
    val email: String,
    val password: String,
    val nickname: String,
)

data class LoginCommand(
    val email: String,
    val password: String,
)

data class LoginResult(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: UserInfoResult,
)

data class UserInfoResult(
    val id: Long,
    val email: String,
    val nickname: String,
    val weightUnit: String,
)

data class TokenResult(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
)

interface RegisterUseCase {
    fun register(command: RegisterCommand): LoginResult
}

interface LoginUseCase {
    fun login(command: LoginCommand): LoginResult
}

interface RefreshTokenUseCase {
    fun refresh(refreshToken: String): TokenResult
}

interface LogoutUseCase {
    fun logout(refreshToken: String)
}

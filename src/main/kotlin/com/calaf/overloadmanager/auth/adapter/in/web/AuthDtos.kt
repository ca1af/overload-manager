package com.calaf.overloadmanager.auth.adapter.`in`.web

import com.calaf.overloadmanager.auth.domain.port.`in`.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    val password: String,

    @field:NotBlank(message = "Nickname is required")
    @field:Size(min = 2, max = 50, message = "Nickname must be between 2 and 50 characters")
    val nickname: String,
)

data class LoginRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String,
)

data class RefreshRequest(
    @field:NotBlank(message = "Refresh token is required")
    val refreshToken: String,
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: UserInfo,
)

data class UserInfo(
    val id: Long,
    val email: String,
    val nickname: String,
    val weightUnit: String,
)

// Mappers
fun RegisterRequest.toCommand() = RegisterCommand(
    email = email,
    password = password,
    nickname = nickname,
)

fun LoginRequest.toCommand() = LoginCommand(
    email = email,
    password = password,
)

fun LoginResult.toResponse() = LoginResponse(
    accessToken = accessToken,
    refreshToken = refreshToken,
    tokenType = tokenType,
    expiresIn = expiresIn,
    user = UserInfo(
        id = user.id,
        email = user.email,
        nickname = user.nickname,
        weightUnit = user.weightUnit,
    ),
)

fun TokenResult.toResponse() = TokenResponse(
    accessToken = accessToken,
    refreshToken = refreshToken,
    tokenType = tokenType,
    expiresIn = expiresIn,
)

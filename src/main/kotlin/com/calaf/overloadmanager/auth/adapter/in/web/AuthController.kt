package com.calaf.overloadmanager.auth.adapter.`in`.web

import com.calaf.overloadmanager.auth.domain.port.`in`.*
import com.calaf.overloadmanager.common.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val logoutUseCase: LogoutUseCase,
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        val result = registerUseCase.register(request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result.toResponse()))
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        val result = loginUseCase.login(request.toCommand())
        return ResponseEntity.ok(ApiResponse.success(result.toResponse()))
    }

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody request: RefreshRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val result = refreshTokenUseCase.refresh(request.refreshToken)
        return ResponseEntity.ok(ApiResponse.success(result.toResponse()))
    }

    @PostMapping("/logout")
    fun logout(@RequestBody request: RefreshRequest): ResponseEntity<Void> {
        logoutUseCase.logout(request.refreshToken)
        return ResponseEntity.noContent().build()
    }
}

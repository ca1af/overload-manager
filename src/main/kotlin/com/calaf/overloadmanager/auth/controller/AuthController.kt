package com.calaf.overloadmanager.auth.controller

import com.calaf.overloadmanager.auth.dto.*
import com.calaf.overloadmanager.auth.service.AuthService
import com.calaf.overloadmanager.common.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        val response = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response))
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        val response = authService.login(request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody request: RefreshRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val response = authService.refresh(request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping("/logout")
    fun logout(@RequestBody request: RefreshRequest): ResponseEntity<Void> {
        authService.logout(request.refreshToken)
        return ResponseEntity.noContent().build()
    }
}

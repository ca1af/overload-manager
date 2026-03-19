package com.calaf.overloadmanager.user.adapter.`in`.web

import com.calaf.overloadmanager.common.ApiResponse
import com.calaf.overloadmanager.user.domain.port.`in`.*
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
) {

    @GetMapping("/me")
    fun getProfile(auth: Authentication): ResponseEntity<ApiResponse<UserProfileResponse>> {
        val userId = auth.principal as Long
        val result = getProfileUseCase.getProfile(userId)
        return ResponseEntity.ok(ApiResponse.success(result.toResponse()))
    }

    @PatchMapping("/me")
    fun updateProfile(
        auth: Authentication,
        @Valid @RequestBody request: UpdateProfileRequest,
    ): ResponseEntity<ApiResponse<UserProfileResponse>> {
        val userId = auth.principal as Long
        val result = updateProfileUseCase.updateProfile(userId, request.toCommand())
        return ResponseEntity.ok(ApiResponse.success(result.toResponse()))
    }

    @DeleteMapping("/me")
    fun deleteAccount(auth: Authentication): ResponseEntity<Void> {
        val userId = auth.principal as Long
        deleteAccountUseCase.deleteAccount(userId)
        return ResponseEntity.noContent().build()
    }
}

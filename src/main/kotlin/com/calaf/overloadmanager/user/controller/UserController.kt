package com.calaf.overloadmanager.user.controller

import com.calaf.overloadmanager.common.ApiResponse
import com.calaf.overloadmanager.user.dto.UpdateProfileRequest
import com.calaf.overloadmanager.user.dto.UserProfileResponse
import com.calaf.overloadmanager.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {

    @GetMapping("/me")
    fun getProfile(auth: Authentication): ResponseEntity<ApiResponse<UserProfileResponse>> {
        val userId = auth.principal as Long
        return ResponseEntity.ok(ApiResponse.success(userService.getProfile(userId)))
    }

    @PatchMapping("/me")
    fun updateProfile(
        auth: Authentication,
        @Valid @RequestBody request: UpdateProfileRequest,
    ): ResponseEntity<ApiResponse<UserProfileResponse>> {
        val userId = auth.principal as Long
        return ResponseEntity.ok(ApiResponse.success(userService.updateProfile(userId, request)))
    }

    @DeleteMapping("/me")
    fun deleteAccount(auth: Authentication): ResponseEntity<Void> {
        val userId = auth.principal as Long
        userService.softDelete(userId)
        return ResponseEntity.noContent().build()
    }
}

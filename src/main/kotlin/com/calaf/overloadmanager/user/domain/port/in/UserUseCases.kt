package com.calaf.overloadmanager.user.domain.port.`in`

import com.calaf.overloadmanager.user.domain.model.WeightUnit
import java.time.LocalDateTime

data class UserProfileResult(
    val id: Long,
    val email: String,
    val nickname: String,
    val weightUnit: WeightUnit,
    val weeklyGoalSessions: Int,
    val emailVerified: Boolean,
    val createdAt: LocalDateTime,
)

data class UpdateProfileCommand(
    val nickname: String? = null,
    val weightUnit: WeightUnit? = null,
    val weeklyGoalSessions: Int? = null,
)

interface GetProfileUseCase {
    fun getProfile(userId: Long): UserProfileResult
}

interface UpdateProfileUseCase {
    fun updateProfile(userId: Long, command: UpdateProfileCommand): UserProfileResult
}

interface DeleteAccountUseCase {
    fun deleteAccount(userId: Long)
}

package com.calaf.overloadmanager.user.adapter.`in`.web

import com.calaf.overloadmanager.user.domain.model.WeightUnit
import com.calaf.overloadmanager.user.domain.port.`in`.UpdateProfileCommand
import com.calaf.overloadmanager.user.domain.port.`in`.UserProfileResult
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class UserProfileResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val weightUnit: WeightUnit,
    val weeklyGoalSessions: Int,
    val emailVerified: Boolean,
    val createdAt: LocalDateTime,
)

data class UpdateProfileRequest(
    @field:Size(min = 2, max = 50, message = "Nickname must be between 2 and 50 characters")
    val nickname: String? = null,

    val weightUnit: WeightUnit? = null,

    @field:Min(1, message = "Weekly goal must be at least 1")
    @field:Max(7, message = "Weekly goal must be at most 7")
    val weeklyGoalSessions: Int? = null,
)

fun UpdateProfileRequest.toCommand() = UpdateProfileCommand(
    nickname = nickname,
    weightUnit = weightUnit,
    weeklyGoalSessions = weeklyGoalSessions,
)

fun UserProfileResult.toResponse() = UserProfileResponse(
    id = id,
    email = email,
    nickname = nickname,
    weightUnit = weightUnit,
    weeklyGoalSessions = weeklyGoalSessions,
    emailVerified = emailVerified,
    createdAt = createdAt,
)

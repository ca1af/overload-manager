package com.calaf.overloadmanager.user.domain.model

import java.time.LocalDateTime

data class User(
    val id: Long = 0,
    val email: String,
    val passwordHash: String,
    val nickname: String,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val weeklyGoalSessions: Int = 4,
    val emailVerified: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null,
)

enum class WeightUnit {
    KG, LB
}

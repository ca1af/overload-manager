package com.calaf.overloadmanager.auth.domain.model

import java.time.LocalDateTime

data class RefreshToken(
    val id: Long = 0,
    val userId: Long,
    val token: String,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)

package com.calaf.overloadmanager.auth.repository

import com.calaf.overloadmanager.auth.domain.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): RefreshToken?
    fun deleteByToken(token: String)
    fun deleteAllByUserId(userId: Long)
}

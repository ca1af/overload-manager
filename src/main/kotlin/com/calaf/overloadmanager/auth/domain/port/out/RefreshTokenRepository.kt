package com.calaf.overloadmanager.auth.domain.port.out

import com.calaf.overloadmanager.auth.domain.model.RefreshToken

interface RefreshTokenRepository {
    fun findByToken(token: String): RefreshToken?
    fun save(refreshToken: RefreshToken): RefreshToken
    fun deleteByToken(token: String)
    fun delete(refreshToken: RefreshToken)
    fun deleteAllByUserId(userId: Long)
}

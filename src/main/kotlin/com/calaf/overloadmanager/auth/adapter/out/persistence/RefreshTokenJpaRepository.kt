package com.calaf.overloadmanager.auth.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenJpaEntity, Long> {
    fun findByToken(token: String): RefreshTokenJpaEntity?
    fun deleteByToken(token: String)
    fun deleteAllByUserId(userId: Long)
}

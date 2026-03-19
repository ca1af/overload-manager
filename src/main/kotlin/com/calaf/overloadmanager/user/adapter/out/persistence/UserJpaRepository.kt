package com.calaf.overloadmanager.user.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserJpaEntity, Long> {
    fun findByEmailAndDeletedAtIsNull(email: String): UserJpaEntity?
    fun findByIdAndDeletedAtIsNull(id: Long): UserJpaEntity?
    fun existsByEmailAndDeletedAtIsNull(email: String): Boolean
}

package com.calaf.overloadmanager.user.repository

import com.calaf.overloadmanager.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmailAndDeletedAtIsNull(email: String): User?
    fun findByIdAndDeletedAtIsNull(id: Long): User?
    fun existsByEmailAndDeletedAtIsNull(email: String): Boolean
}

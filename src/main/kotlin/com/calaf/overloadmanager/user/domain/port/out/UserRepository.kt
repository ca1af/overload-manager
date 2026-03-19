package com.calaf.overloadmanager.user.domain.port.out

import com.calaf.overloadmanager.user.domain.model.User

interface UserRepository {
    fun findById(id: Long): User?
    fun findByEmail(email: String): User?
    fun save(user: User): User
    fun existsByEmail(email: String): Boolean
}

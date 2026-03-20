package com.calaf.overloadmanager.user.adapter.out.persistence

import com.calaf.overloadmanager.user.domain.model.User
import com.calaf.overloadmanager.user.domain.port.out.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserPersistenceAdapter(
    private val jpaRepository: UserJpaRepository,
) : UserRepository {

    override fun findById(id: Long): User? =
        jpaRepository.findByIdAndDeletedAtIsNull(id)?.toDomain()

    override fun findByEmail(email: String): User? =
        jpaRepository.findByEmailAndDeletedAtIsNull(email)?.toDomain()

    override fun save(user: User): User =
        jpaRepository.save(user.toJpaEntity()).toDomain()

    override fun existsByEmail(email: String): Boolean =
        jpaRepository.existsByEmailAndDeletedAtIsNull(email)
}

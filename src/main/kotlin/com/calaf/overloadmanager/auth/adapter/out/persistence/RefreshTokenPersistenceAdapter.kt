package com.calaf.overloadmanager.auth.adapter.out.persistence

import com.calaf.overloadmanager.auth.domain.model.RefreshToken
import com.calaf.overloadmanager.auth.domain.port.out.RefreshTokenRepository
import com.calaf.overloadmanager.user.adapter.out.persistence.UserJpaRepository
import org.springframework.stereotype.Repository

@Repository
class RefreshTokenPersistenceAdapter(
    private val jpaRepository: RefreshTokenJpaRepository,
    private val userJpaRepository: UserJpaRepository,
) : RefreshTokenRepository {

    override fun findByToken(token: String): RefreshToken? =
        jpaRepository.findByToken(token)?.toDomain()

    override fun save(refreshToken: RefreshToken): RefreshToken {
        val userEntity = userJpaRepository.findById(refreshToken.userId).orElseThrow()
        val entity = RefreshTokenJpaEntity(
            id = refreshToken.id,
            user = userEntity,
            token = refreshToken.token,
            expiresAt = refreshToken.expiresAt,
        )
        return jpaRepository.save(entity).toDomain()
    }

    override fun deleteByToken(token: String) {
        jpaRepository.deleteByToken(token)
    }

    override fun delete(refreshToken: RefreshToken) {
        jpaRepository.deleteById(refreshToken.id)
    }

    override fun deleteAllByUserId(userId: Long) {
        jpaRepository.deleteAllByUserId(userId)
    }
}

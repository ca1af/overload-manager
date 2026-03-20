package com.calaf.overloadmanager.auth.adapter.out.persistence

import com.calaf.overloadmanager.auth.domain.model.RefreshToken
import com.calaf.overloadmanager.user.adapter.out.persistence.UserJpaEntity
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_tokens")
@EntityListeners(AuditingEntityListener::class)
class RefreshTokenJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserJpaEntity,

    @Column(nullable = false, unique = true, length = 512)
    val token: String,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: LocalDateTime,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
)

fun RefreshTokenJpaEntity.toDomain() = RefreshToken(
    id = id,
    userId = user.id,
    token = token,
    expiresAt = expiresAt,
    createdAt = createdAt,
)

package com.calaf.overloadmanager.user.adapter.out.persistence

import com.calaf.overloadmanager.user.domain.model.User
import com.calaf.overloadmanager.user.domain.model.WeightUnit
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
class UserJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(name = "password_hash", nullable = false, length = 255)
    var passwordHash: String,

    @Column(nullable = false, length = 50)
    var nickname: String,

    @Column(name = "weight_unit", nullable = false, length = 2)
    @Enumerated(EnumType.STRING)
    var weightUnit: WeightUnit = WeightUnit.KG,

    @Column(name = "weekly_goal_sessions", nullable = false)
    var weeklyGoalSessions: Int = 4,

    @Column(name = "email_verified", nullable = false)
    var emailVerified: Boolean = false,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,
)

fun UserJpaEntity.toDomain() = User(
    id = id,
    email = email,
    passwordHash = passwordHash,
    nickname = nickname,
    weightUnit = weightUnit,
    weeklyGoalSessions = weeklyGoalSessions,
    emailVerified = emailVerified,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
)

fun User.toJpaEntity() = UserJpaEntity(
    id = id,
    email = email,
    passwordHash = passwordHash,
    nickname = nickname,
    weightUnit = weightUnit,
    weeklyGoalSessions = weeklyGoalSessions,
    emailVerified = emailVerified,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
)

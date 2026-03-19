package com.calaf.overloadmanager.workout.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "workout_sets")
@EntityListeners(AuditingEntityListener::class)
class WorkoutSet(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_exercise_id", nullable = false)
    val sessionExercise: SessionExercise,

    @Column(name = "set_number", nullable = false)
    var setNumber: Int,

    @Column(nullable = false, precision = 7, scale = 2)
    var weight: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    var reps: Int = 0,

    @Column(nullable = false)
    var completed: Boolean = false,

    @Column(name = "rest_seconds")
    var restSeconds: Int? = null,

    @Column(name = "completed_at")
    var completedAt: LocalDateTime? = null,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)

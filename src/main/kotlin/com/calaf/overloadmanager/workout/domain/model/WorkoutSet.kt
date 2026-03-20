package com.calaf.overloadmanager.workout.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class WorkoutSet(
    val id: Long = 0,
    val sessionExerciseId: Long = 0,
    val setNumber: Int,
    val weight: BigDecimal = BigDecimal.ZERO,
    val reps: Int = 0,
    val completed: Boolean = false,
    val restSeconds: Int? = null,
    val completedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)

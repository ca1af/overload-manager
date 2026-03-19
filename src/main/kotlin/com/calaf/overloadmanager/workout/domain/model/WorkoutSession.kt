package com.calaf.overloadmanager.workout.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class WorkoutSession(
    val id: Long = 0,
    val userId: Long,
    val sessionDate: LocalDate,
    val notes: String? = null,
    val startedAt: LocalDateTime? = null,
    val finishedAt: LocalDateTime? = null,
    val durationSeconds: Int? = null,
    val exercises: List<SessionExercise> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)

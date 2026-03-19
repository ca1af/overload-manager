package com.calaf.overloadmanager.workout.domain.model

import java.time.LocalDateTime

data class SessionExercise(
    val id: Long = 0,
    val sessionId: Long = 0,
    val exerciseId: Long,
    val exerciseNameKo: String = "",
    val exerciseNameEn: String = "",
    val exerciseCategory: String = "",
    val orderIndex: Int,
    val sets: List<WorkoutSet> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
)

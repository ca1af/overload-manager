package com.calaf.overloadmanager.exercise.domain.port.out

import java.math.BigDecimal
import java.time.LocalDate

data class SessionExerciseData(
    val sessionExerciseId: Long,
    val sessionId: Long,
    val sessionDate: LocalDate,
    val sets: List<SetData>,
)

data class SetData(
    val setNumber: Int,
    val weight: BigDecimal,
    val reps: Int,
    val completed: Boolean,
)

interface ExerciseHistoryRepository {
    fun findPreviousSessionExercise(
        userId: Long,
        exerciseId: Long,
        excludeSessionId: Long,
    ): List<SessionExerciseData>

    fun findExerciseHistory(
        userId: Long,
        exerciseId: Long,
    ): List<SessionExerciseData>
}

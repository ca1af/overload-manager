package com.calaf.overloadmanager.workout.domain.port.`in`

import java.math.BigDecimal

data class CreateSetCommand(
    val weight: BigDecimal = BigDecimal.ZERO,
    val reps: Int = 0,
    val completed: Boolean = false,
    val restSeconds: Int? = null,
)

data class UpdateSetCommand(
    val weight: BigDecimal? = null,
    val reps: Int? = null,
    val completed: Boolean? = null,
    val restSeconds: Int? = null,
)

interface CreateSetUseCase {
    fun createSet(userId: Long, sessionId: Long, sessionExerciseId: Long, command: CreateSetCommand): WorkoutSetResult
}

interface UpdateSetUseCase {
    fun updateSet(userId: Long, sessionId: Long, sessionExerciseId: Long, setId: Long, command: UpdateSetCommand): WorkoutSetResult
}

interface DeleteSetUseCase {
    fun deleteSet(userId: Long, sessionId: Long, sessionExerciseId: Long, setId: Long)
}

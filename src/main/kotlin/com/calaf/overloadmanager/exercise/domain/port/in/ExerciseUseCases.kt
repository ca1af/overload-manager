package com.calaf.overloadmanager.exercise.domain.port.`in`

import com.calaf.overloadmanager.exercise.domain.model.Equipment
import com.calaf.overloadmanager.exercise.domain.model.ExerciseCategory
import com.calaf.overloadmanager.exercise.domain.model.ExerciseType
import java.math.BigDecimal
import java.time.LocalDate

data class ExerciseResult(
    val id: Long,
    val nameKo: String,
    val nameEn: String,
    val category: ExerciseCategory,
    val exerciseType: ExerciseType,
    val equipment: Equipment,
    val primaryMuscle: String,
    val secondaryMuscles: List<String>,
    val defaultSetsMin: Int,
    val defaultSetsMax: Int,
    val defaultRepsMin: Int,
    val defaultRepsMax: Int,
    val isCustom: Boolean,
)

data class PreviousSessionResult(
    val sessionId: Long,
    val sessionDate: LocalDate,
    val sets: List<PreviousSetResult>,
)

data class PreviousSetResult(
    val setNumber: Int,
    val weight: BigDecimal,
    val reps: Int,
    val completed: Boolean,
)

data class ExerciseHistoryResult(
    val sessionId: Long,
    val sessionDate: LocalDate,
    val maxWeightKg: BigDecimal,
    val totalVolumeKg: BigDecimal,
    val estimatedOneRepMax: BigDecimal,
    val sets: List<PreviousSetResult>,
)

interface ListExercisesUseCase {
    fun listExercises(
        userId: Long?,
        category: ExerciseCategory?,
        search: String?,
        page: Int,
        size: Int,
    ): PageResult<ExerciseResult>
}

data class PageResult<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean,
)

interface GetExerciseUseCase {
    fun getExercise(id: Long): ExerciseResult
}

interface GetPreviousSessionUseCase {
    fun getPreviousSession(userId: Long, exerciseId: Long, excludeSessionId: Long): PreviousSessionResult?
}

interface GetExerciseHistoryUseCase {
    fun getExerciseHistory(userId: Long, exerciseId: Long): List<ExerciseHistoryResult>
}

data class CreateExerciseCommand(
    val nameKo: String,
    val category: ExerciseCategory,
)

interface CreateExerciseUseCase {
    fun createExercise(userId: Long, command: CreateExerciseCommand): ExerciseResult
}

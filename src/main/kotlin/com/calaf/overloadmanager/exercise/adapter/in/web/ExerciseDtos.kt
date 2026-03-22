package com.calaf.overloadmanager.exercise.adapter.`in`.web

import com.calaf.overloadmanager.exercise.domain.model.Equipment
import com.calaf.overloadmanager.exercise.domain.model.ExerciseCategory
import com.calaf.overloadmanager.exercise.domain.model.ExerciseType
import com.calaf.overloadmanager.exercise.domain.port.`in`.*
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import java.time.LocalDate

data class CreateExerciseRequest(
    @field:NotBlank(message = "Exercise name is required")
    val nameKo: String,
    val category: ExerciseCategory = ExerciseCategory.CHEST,
) {
    fun toCommand() = CreateExerciseCommand(
        nameKo = nameKo,
        category = category,
    )
}

data class ExerciseResponse(
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

data class PreviousSessionResponse(
    val sessionId: Long,
    val sessionDate: LocalDate,
    val sets: List<PreviousSetResponse>,
)

data class PreviousSetResponse(
    val setNumber: Int,
    val weight: BigDecimal,
    val reps: Int,
    val completed: Boolean,
)

data class ExerciseHistoryResponse(
    val sessionId: Long,
    val sessionDate: LocalDate,
    val maxWeightKg: BigDecimal,
    val totalVolumeKg: BigDecimal,
    val estimatedOneRepMax: BigDecimal,
    val sets: List<PreviousSetResponse>,
)

fun ExerciseResult.toResponse() = ExerciseResponse(
    id = id,
    nameKo = nameKo,
    nameEn = nameEn,
    category = category,
    exerciseType = exerciseType,
    equipment = equipment,
    primaryMuscle = primaryMuscle,
    secondaryMuscles = secondaryMuscles,
    defaultSetsMin = defaultSetsMin,
    defaultSetsMax = defaultSetsMax,
    defaultRepsMin = defaultRepsMin,
    defaultRepsMax = defaultRepsMax,
    isCustom = isCustom,
)

fun PreviousSessionResult.toResponse() = PreviousSessionResponse(
    sessionId = sessionId,
    sessionDate = sessionDate,
    sets = sets.map { it.toResponse() },
)

fun PreviousSetResult.toResponse() = PreviousSetResponse(
    setNumber = setNumber,
    weight = weight,
    reps = reps,
    completed = completed,
)

fun ExerciseHistoryResult.toResponse() = ExerciseHistoryResponse(
    sessionId = sessionId,
    sessionDate = sessionDate,
    maxWeightKg = maxWeightKg,
    totalVolumeKg = totalVolumeKg,
    estimatedOneRepMax = estimatedOneRepMax,
    sets = sets.map { it.toResponse() },
)

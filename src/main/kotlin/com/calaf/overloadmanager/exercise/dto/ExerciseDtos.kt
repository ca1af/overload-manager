package com.calaf.overloadmanager.exercise.dto

import com.calaf.overloadmanager.exercise.domain.Equipment
import com.calaf.overloadmanager.exercise.domain.ExerciseCategory
import com.calaf.overloadmanager.exercise.domain.ExerciseType
import java.math.BigDecimal
import java.time.LocalDate

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

data class ExerciseHistoryEntry(
    val sessionId: Long,
    val sessionDate: LocalDate,
    val maxWeightKg: BigDecimal,
    val totalVolumeKg: BigDecimal,
    val estimatedOneRepMax: BigDecimal,
    val sets: List<PreviousSetResponse>,
)

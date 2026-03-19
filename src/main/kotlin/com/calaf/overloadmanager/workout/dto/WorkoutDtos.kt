package com.calaf.overloadmanager.workout.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

// Session DTOs
data class CreateSessionRequest(
    @field:NotNull(message = "Session date is required")
    val sessionDate: LocalDate,
    val notes: String? = null,
    val startedAt: LocalDateTime? = null,
)

data class UpdateSessionRequest(
    val notes: String? = null,
    val finishedAt: LocalDateTime? = null,
    val durationSeconds: Int? = null,
)

data class SessionSummaryResponse(
    val id: Long,
    val sessionDate: LocalDate,
    val notes: String?,
    val startedAt: LocalDateTime?,
    val finishedAt: LocalDateTime?,
    val durationSeconds: Int?,
    val exerciseCount: Int,
    val createdAt: LocalDateTime,
)

data class SessionDetailResponse(
    val id: Long,
    val sessionDate: LocalDate,
    val notes: String?,
    val startedAt: LocalDateTime?,
    val finishedAt: LocalDateTime?,
    val durationSeconds: Int?,
    val exercises: List<SessionExerciseResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class SessionExerciseResponse(
    val id: Long,
    val exerciseId: Long,
    val exerciseNameKo: String,
    val exerciseNameEn: String,
    val category: String,
    val orderIndex: Int,
    val sets: List<WorkoutSetResponse>,
)

// Exercise in session DTOs
data class AddExercisesRequest(
    val exerciseIds: List<Long>,
)

data class AddExerciseResponse(
    val id: Long,
    val exerciseId: Long,
    val orderIndex: Int,
)

// Set DTOs
data class CreateSetRequest(
    @field:PositiveOrZero(message = "Weight must be zero or positive")
    val weight: BigDecimal = BigDecimal.ZERO,

    @field:PositiveOrZero(message = "Reps must be zero or positive")
    val reps: Int = 0,

    val completed: Boolean = false,
    val restSeconds: Int? = null,
)

data class UpdateSetRequest(
    @field:PositiveOrZero(message = "Weight must be zero or positive")
    val weight: BigDecimal? = null,

    @field:PositiveOrZero(message = "Reps must be zero or positive")
    val reps: Int? = null,

    val completed: Boolean? = null,
    val restSeconds: Int? = null,
)

data class WorkoutSetResponse(
    val id: Long,
    val setNumber: Int,
    val weight: BigDecimal,
    val reps: Int,
    val completed: Boolean,
    val restSeconds: Int?,
    val completedAt: LocalDateTime?,
)

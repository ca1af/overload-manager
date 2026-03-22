package com.calaf.overloadmanager.workout.adapter.`in`.web

import com.calaf.overloadmanager.workout.domain.port.`in`.*
import jakarta.validation.constraints.NotNull
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
    val weight: BigDecimal? = null,

    @field:PositiveOrZero(message = "Reps must be zero or positive")
    val reps: Int? = null,

    val completed: Boolean? = null,
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

// Mappers
fun CreateSessionRequest.toCommand() = CreateSessionCommand(
    sessionDate = sessionDate,
    notes = notes,
    startedAt = startedAt,
)

fun UpdateSessionRequest.toCommand() = UpdateSessionCommand(
    notes = notes,
    finishedAt = finishedAt,
    durationSeconds = durationSeconds,
)

fun AddExercisesRequest.toCommand() = AddExercisesCommand(
    exerciseIds = exerciseIds,
)

fun CreateSetRequest.toCommand() = CreateSetCommand(
    weight = weight ?: BigDecimal.ZERO,
    reps = reps ?: 0,
    completed = completed ?: false,
    restSeconds = restSeconds,
)

fun UpdateSetRequest.toCommand() = UpdateSetCommand(
    weight = weight,
    reps = reps,
    completed = completed,
    restSeconds = restSeconds,
)

fun SessionSummaryResult.toResponse() = SessionSummaryResponse(
    id = id,
    sessionDate = sessionDate,
    notes = notes,
    startedAt = startedAt,
    finishedAt = finishedAt,
    durationSeconds = durationSeconds,
    exerciseCount = exerciseCount,
    createdAt = createdAt,
)

fun SessionDetailResult.toResponse() = SessionDetailResponse(
    id = id,
    sessionDate = sessionDate,
    notes = notes,
    startedAt = startedAt,
    finishedAt = finishedAt,
    durationSeconds = durationSeconds,
    exercises = exercises.map { it.toResponse() },
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun SessionExerciseResult.toResponse() = SessionExerciseResponse(
    id = id,
    exerciseId = exerciseId,
    exerciseNameKo = exerciseNameKo,
    exerciseNameEn = exerciseNameEn,
    category = category,
    orderIndex = orderIndex,
    sets = sets.map { it.toResponse() },
)

fun WorkoutSetResult.toResponse() = WorkoutSetResponse(
    id = id,
    setNumber = setNumber,
    weight = weight,
    reps = reps,
    completed = completed,
    restSeconds = restSeconds,
    completedAt = completedAt,
)

fun AddExerciseResult.toResponse() = AddExerciseResponse(
    id = id,
    exerciseId = exerciseId,
    orderIndex = orderIndex,
)

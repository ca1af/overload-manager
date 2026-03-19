package com.calaf.overloadmanager.workout.domain.port.`in`

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

// Session results
data class SessionSummaryResult(
    val id: Long,
    val sessionDate: LocalDate,
    val notes: String?,
    val startedAt: LocalDateTime?,
    val finishedAt: LocalDateTime?,
    val durationSeconds: Int?,
    val exerciseCount: Int,
    val createdAt: LocalDateTime,
)

data class SessionDetailResult(
    val id: Long,
    val sessionDate: LocalDate,
    val notes: String?,
    val startedAt: LocalDateTime?,
    val finishedAt: LocalDateTime?,
    val durationSeconds: Int?,
    val exercises: List<SessionExerciseResult>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class SessionExerciseResult(
    val id: Long,
    val exerciseId: Long,
    val exerciseNameKo: String,
    val exerciseNameEn: String,
    val category: String,
    val orderIndex: Int,
    val sets: List<WorkoutSetResult>,
)

data class WorkoutSetResult(
    val id: Long,
    val setNumber: Int,
    val weight: BigDecimal,
    val reps: Int,
    val completed: Boolean,
    val restSeconds: Int?,
    val completedAt: LocalDateTime?,
)

data class AddExerciseResult(
    val id: Long,
    val exerciseId: Long,
    val orderIndex: Int,
)

// Commands
data class CreateSessionCommand(
    val sessionDate: LocalDate,
    val notes: String? = null,
    val startedAt: LocalDateTime? = null,
)

data class UpdateSessionCommand(
    val notes: String? = null,
    val finishedAt: LocalDateTime? = null,
    val durationSeconds: Int? = null,
)

data class AddExercisesCommand(
    val exerciseIds: List<Long>,
)

// Page result
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

// Use cases
interface ListSessionsUseCase {
    fun listSessions(userId: Long, startDate: LocalDate?, endDate: LocalDate?, page: Int, size: Int): PageResult<SessionSummaryResult>
}

interface GetSessionUseCase {
    fun getSession(userId: Long, sessionId: Long): SessionDetailResult
}

interface CreateSessionUseCase {
    fun createSession(userId: Long, command: CreateSessionCommand): SessionDetailResult
}

interface UpdateSessionUseCase {
    fun updateSession(userId: Long, sessionId: Long, command: UpdateSessionCommand): SessionDetailResult
}

interface DeleteSessionUseCase {
    fun deleteSession(userId: Long, sessionId: Long)
}

interface AddExercisesToSessionUseCase {
    fun addExercises(userId: Long, sessionId: Long, command: AddExercisesCommand): List<AddExerciseResult>
}

interface RemoveExerciseFromSessionUseCase {
    fun removeExercise(userId: Long, sessionId: Long, sessionExerciseId: Long)
}

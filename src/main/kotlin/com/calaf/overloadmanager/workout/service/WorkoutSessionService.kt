package com.calaf.overloadmanager.workout.service

import com.calaf.overloadmanager.common.AppException
import com.calaf.overloadmanager.common.ErrorCode
import com.calaf.overloadmanager.exercise.repository.ExerciseRepository
import com.calaf.overloadmanager.user.repository.UserRepository
import com.calaf.overloadmanager.workout.domain.SessionExercise
import com.calaf.overloadmanager.workout.domain.WorkoutSession
import com.calaf.overloadmanager.workout.dto.*
import com.calaf.overloadmanager.workout.repository.SessionExerciseRepository
import com.calaf.overloadmanager.workout.repository.WorkoutSessionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class WorkoutSessionService(
    private val sessionRepository: WorkoutSessionRepository,
    private val sessionExerciseRepository: SessionExerciseRepository,
    private val exerciseRepository: ExerciseRepository,
    private val userRepository: UserRepository,
) {

    fun listSessions(
        userId: Long,
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageable: Pageable,
    ): Page<SessionSummaryResponse> {
        return sessionRepository.findByUserIdWithDateRange(userId, startDate, endDate, pageable)
            .map { it.toSummary() }
    }

    fun getSession(userId: Long, sessionId: Long): SessionDetailResponse {
        val session = findUserSession(userId, sessionId)
        return session.toDetail()
    }

    @Transactional
    fun createSession(userId: Long, request: CreateSessionRequest): SessionDetailResponse {
        val user = userRepository.findByIdAndDeletedAtIsNull(userId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "User not found")

        val session = sessionRepository.save(
            WorkoutSession(
                user = user,
                sessionDate = request.sessionDate,
                notes = request.notes,
                startedAt = request.startedAt,
            )
        )
        return session.toDetail()
    }

    @Transactional
    fun updateSession(userId: Long, sessionId: Long, request: UpdateSessionRequest): SessionDetailResponse {
        val session = findUserSession(userId, sessionId)
        request.notes?.let { session.notes = it }
        request.finishedAt?.let { session.finishedAt = it }
        request.durationSeconds?.let { session.durationSeconds = it }
        return sessionRepository.save(session).toDetail()
    }

    @Transactional
    fun deleteSession(userId: Long, sessionId: Long) {
        val session = findUserSession(userId, sessionId)
        sessionRepository.delete(session)
    }

    @Transactional
    fun addExercises(userId: Long, sessionId: Long, request: AddExercisesRequest): List<AddExerciseResponse> {
        val session = findUserSession(userId, sessionId)
        var maxOrder = sessionExerciseRepository.findMaxOrderIndex(sessionId)

        return request.exerciseIds.map { exerciseId ->
            val exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow { AppException(ErrorCode.NOT_FOUND, "Exercise $exerciseId not found") }

            maxOrder++
            val se = sessionExerciseRepository.save(
                SessionExercise(
                    session = session,
                    exercise = exercise,
                    orderIndex = maxOrder,
                )
            )
            AddExerciseResponse(id = se.id, exerciseId = exercise.id, orderIndex = se.orderIndex)
        }
    }

    @Transactional
    fun removeExercise(userId: Long, sessionId: Long, sessionExerciseId: Long) {
        findUserSession(userId, sessionId)
        val se = sessionExerciseRepository.findByIdAndSessionId(sessionExerciseId, sessionId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "Session exercise not found")
        sessionExerciseRepository.delete(se)
    }

    private fun findUserSession(userId: Long, sessionId: Long): WorkoutSession {
        return sessionRepository.findByIdAndUserId(sessionId, userId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "Workout session not found")
    }

    private fun WorkoutSession.toSummary() = SessionSummaryResponse(
        id = id,
        sessionDate = sessionDate,
        notes = notes,
        startedAt = startedAt,
        finishedAt = finishedAt,
        durationSeconds = durationSeconds,
        exerciseCount = exercises.size,
        createdAt = createdAt,
    )

    private fun WorkoutSession.toDetail() = SessionDetailResponse(
        id = id,
        sessionDate = sessionDate,
        notes = notes,
        startedAt = startedAt,
        finishedAt = finishedAt,
        durationSeconds = durationSeconds,
        exercises = exercises.map { se ->
            val ex = se.exercise
            SessionExerciseResponse(
                id = se.id,
                exerciseId = ex.id,
                exerciseNameKo = ex.nameKo,
                exerciseNameEn = ex.nameEn,
                category = ex.category.name,
                orderIndex = se.orderIndex,
                sets = se.sets.map { s ->
                    WorkoutSetResponse(
                        id = s.id,
                        setNumber = s.setNumber,
                        weight = s.weight,
                        reps = s.reps,
                        completed = s.completed,
                        restSeconds = s.restSeconds,
                        completedAt = s.completedAt,
                    )
                },
            )
        },
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

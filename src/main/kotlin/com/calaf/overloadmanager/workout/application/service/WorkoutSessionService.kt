package com.calaf.overloadmanager.workout.application.service

import com.calaf.overloadmanager.common.AppException
import com.calaf.overloadmanager.common.ErrorCode
import com.calaf.overloadmanager.exercise.domain.port.out.ExerciseRepository
import com.calaf.overloadmanager.user.domain.port.out.UserRepository
import com.calaf.overloadmanager.workout.domain.model.SessionExercise
import com.calaf.overloadmanager.workout.domain.model.WorkoutSession
import com.calaf.overloadmanager.workout.domain.port.`in`.*
import com.calaf.overloadmanager.workout.domain.port.out.SessionExerciseRepository
import com.calaf.overloadmanager.workout.domain.port.out.WorkoutSessionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class WorkoutSessionService(
    private val sessionRepository: WorkoutSessionRepository,
    private val sessionExerciseRepository: SessionExerciseRepository,
    private val exerciseRepository: ExerciseRepository,
    private val userRepository: UserRepository,
) : ListSessionsUseCase, GetSessionUseCase, CreateSessionUseCase, UpdateSessionUseCase,
    DeleteSessionUseCase, AddExercisesToSessionUseCase, RemoveExerciseFromSessionUseCase {

    override fun listSessions(
        userId: Long,
        startDate: java.time.LocalDate?,
        endDate: java.time.LocalDate?,
        page: Int,
        size: Int,
    ): PageResult<SessionSummaryResult> {
        val pageResult = sessionRepository.findByUserIdWithDateRange(userId, startDate, endDate, page, size)
        return PageResult(
            content = pageResult.content.map { it.toSummary() },
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
            number = pageResult.number,
            size = pageResult.size,
            first = pageResult.first,
            last = pageResult.last,
            empty = pageResult.empty,
        )
    }

    override fun getSession(userId: Long, sessionId: Long): SessionDetailResult {
        val session = findUserSession(userId, sessionId)
        return session.toDetail()
    }

    @Transactional
    override fun createSession(userId: Long, command: CreateSessionCommand): SessionDetailResult {
        userRepository.findById(userId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "User not found")

        val session = sessionRepository.save(
            WorkoutSession(
                userId = userId,
                sessionDate = command.sessionDate,
                notes = command.notes,
                startedAt = command.startedAt,
            )
        )
        return session.toDetail()
    }

    @Transactional
    override fun updateSession(userId: Long, sessionId: Long, command: UpdateSessionCommand): SessionDetailResult {
        val session = findUserSession(userId, sessionId)
        val updated = session.copy(
            notes = command.notes ?: session.notes,
            finishedAt = command.finishedAt ?: session.finishedAt,
            durationSeconds = command.durationSeconds ?: session.durationSeconds,
        )
        return sessionRepository.save(updated).toDetail()
    }

    @Transactional
    override fun deleteSession(userId: Long, sessionId: Long) {
        val session = findUserSession(userId, sessionId)
        sessionRepository.delete(session)
    }

    @Transactional
    override fun addExercises(userId: Long, sessionId: Long, command: AddExercisesCommand): List<AddExerciseResult> {
        findUserSession(userId, sessionId)
        var maxOrder = sessionExerciseRepository.findMaxOrderIndex(sessionId)

        return command.exerciseIds.map { exerciseId ->
            val exercise = exerciseRepository.findById(exerciseId)
                ?: throw AppException(ErrorCode.NOT_FOUND, "Exercise $exerciseId not found")

            maxOrder++
            val se = sessionExerciseRepository.save(
                SessionExercise(
                    sessionId = sessionId,
                    exerciseId = exercise.id,
                    orderIndex = maxOrder,
                )
            )
            AddExerciseResult(id = se.id, exerciseId = exercise.id, orderIndex = se.orderIndex)
        }
    }

    @Transactional
    override fun removeExercise(userId: Long, sessionId: Long, sessionExerciseId: Long) {
        findUserSession(userId, sessionId)
        val se = sessionExerciseRepository.findByIdAndSessionId(sessionExerciseId, sessionId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "Session exercise not found")
        sessionExerciseRepository.delete(se)
    }

    private fun findUserSession(userId: Long, sessionId: Long): WorkoutSession {
        return sessionRepository.findByIdAndUserId(sessionId, userId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "Workout session not found")
    }

    private fun WorkoutSession.toSummary() = SessionSummaryResult(
        id = id,
        sessionDate = sessionDate,
        notes = notes,
        startedAt = startedAt,
        finishedAt = finishedAt,
        durationSeconds = durationSeconds,
        exerciseCount = exercises.size,
        createdAt = createdAt,
    )

    private fun WorkoutSession.toDetail() = SessionDetailResult(
        id = id,
        sessionDate = sessionDate,
        notes = notes,
        startedAt = startedAt,
        finishedAt = finishedAt,
        durationSeconds = durationSeconds,
        exercises = exercises.map { se ->
            SessionExerciseResult(
                id = se.id,
                exerciseId = se.exerciseId,
                exerciseNameKo = se.exerciseNameKo,
                exerciseNameEn = se.exerciseNameEn,
                category = se.exerciseCategory,
                orderIndex = se.orderIndex,
                sets = se.sets.map { s ->
                    WorkoutSetResult(
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

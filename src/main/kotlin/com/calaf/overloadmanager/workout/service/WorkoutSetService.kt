package com.calaf.overloadmanager.workout.service

import com.calaf.overloadmanager.common.AppException
import com.calaf.overloadmanager.common.ErrorCode
import com.calaf.overloadmanager.workout.domain.WorkoutSet
import com.calaf.overloadmanager.workout.dto.*
import com.calaf.overloadmanager.workout.repository.SessionExerciseRepository
import com.calaf.overloadmanager.workout.repository.WorkoutSessionRepository
import com.calaf.overloadmanager.workout.repository.WorkoutSetRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class WorkoutSetService(
    private val workoutSetRepository: WorkoutSetRepository,
    private val sessionExerciseRepository: SessionExerciseRepository,
    private val sessionRepository: WorkoutSessionRepository,
) {

    fun createSet(
        userId: Long,
        sessionId: Long,
        sessionExerciseId: Long,
        request: CreateSetRequest,
    ): WorkoutSetResponse {
        verifyOwnership(userId, sessionId)
        val se = sessionExerciseRepository.findByIdAndSessionId(sessionExerciseId, sessionId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "Session exercise not found")

        val setNumber = workoutSetRepository.findMaxSetNumber(sessionExerciseId) + 1
        val set = workoutSetRepository.save(
            WorkoutSet(
                sessionExercise = se,
                setNumber = setNumber,
                weight = request.weight,
                reps = request.reps,
                completed = request.completed,
                restSeconds = request.restSeconds,
                completedAt = if (request.completed) LocalDateTime.now() else null,
            )
        )
        return set.toResponse()
    }

    fun updateSet(
        userId: Long,
        sessionId: Long,
        sessionExerciseId: Long,
        setId: Long,
        request: UpdateSetRequest,
    ): WorkoutSetResponse {
        verifyOwnership(userId, sessionId)
        val set = workoutSetRepository.findByIdAndSessionExerciseId(setId, sessionExerciseId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "Workout set not found")

        request.weight?.let { set.weight = it }
        request.reps?.let { set.reps = it }
        request.completed?.let { completed ->
            set.completed = completed
            if (completed && set.completedAt == null) {
                set.completedAt = LocalDateTime.now()
            } else if (!completed) {
                set.completedAt = null
            }
        }
        request.restSeconds?.let { set.restSeconds = it }

        return workoutSetRepository.save(set).toResponse()
    }

    fun deleteSet(userId: Long, sessionId: Long, sessionExerciseId: Long, setId: Long) {
        verifyOwnership(userId, sessionId)
        val set = workoutSetRepository.findByIdAndSessionExerciseId(setId, sessionExerciseId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "Workout set not found")
        workoutSetRepository.delete(set)
    }

    private fun verifyOwnership(userId: Long, sessionId: Long) {
        sessionRepository.findByIdAndUserId(sessionId, userId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "Workout session not found")
    }

    private fun WorkoutSet.toResponse() = WorkoutSetResponse(
        id = id,
        setNumber = setNumber,
        weight = weight,
        reps = reps,
        completed = completed,
        restSeconds = restSeconds,
        completedAt = completedAt,
    )
}

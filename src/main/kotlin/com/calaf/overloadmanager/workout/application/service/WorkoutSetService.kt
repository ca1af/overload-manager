package com.calaf.overloadmanager.workout.application.service

import com.calaf.overloadmanager.common.AppException
import com.calaf.overloadmanager.common.ErrorCode
import com.calaf.overloadmanager.workout.domain.model.WorkoutSet
import com.calaf.overloadmanager.workout.domain.port.`in`.*
import com.calaf.overloadmanager.workout.domain.port.out.SessionExerciseRepository
import com.calaf.overloadmanager.workout.domain.port.out.WorkoutSessionRepository
import com.calaf.overloadmanager.workout.domain.port.out.WorkoutSetRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class WorkoutSetService(
    private val workoutSetRepository: WorkoutSetRepository,
    private val sessionExerciseRepository: SessionExerciseRepository,
    private val sessionRepository: WorkoutSessionRepository,
) : CreateSetUseCase, UpdateSetUseCase, DeleteSetUseCase {

    override fun createSet(
        userId: Long,
        sessionId: Long,
        sessionExerciseId: Long,
        command: CreateSetCommand,
    ): WorkoutSetResult {
        verifyOwnership(userId, sessionId)
        sessionExerciseRepository.findByIdAndSessionId(sessionExerciseId, sessionId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "Session exercise not found")

        val setNumber = workoutSetRepository.findMaxSetNumber(sessionExerciseId) + 1
        val set = workoutSetRepository.save(
            WorkoutSet(
                sessionExerciseId = sessionExerciseId,
                setNumber = setNumber,
                weight = command.weight,
                reps = command.reps,
                completed = command.completed,
                restSeconds = command.restSeconds,
                completedAt = if (command.completed) LocalDateTime.now() else null,
            )
        )
        return set.toResult()
    }

    override fun updateSet(
        userId: Long,
        sessionId: Long,
        sessionExerciseId: Long,
        setId: Long,
        command: UpdateSetCommand,
    ): WorkoutSetResult {
        verifyOwnership(userId, sessionId)
        val set = workoutSetRepository.findByIdAndSessionExerciseId(setId, sessionExerciseId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "Workout set not found")

        val newCompleted = command.completed ?: set.completed
        val newCompletedAt = when {
            command.completed == true && set.completedAt == null -> LocalDateTime.now()
            command.completed == false -> null
            else -> set.completedAt
        }

        val updated = set.copy(
            weight = command.weight ?: set.weight,
            reps = command.reps ?: set.reps,
            completed = newCompleted,
            restSeconds = command.restSeconds ?: set.restSeconds,
            completedAt = newCompletedAt,
        )

        return workoutSetRepository.save(updated).toResult()
    }

    override fun deleteSet(userId: Long, sessionId: Long, sessionExerciseId: Long, setId: Long) {
        verifyOwnership(userId, sessionId)
        val set = workoutSetRepository.findByIdAndSessionExerciseId(setId, sessionExerciseId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "Workout set not found")
        workoutSetRepository.delete(set)
    }

    private fun verifyOwnership(userId: Long, sessionId: Long) {
        sessionRepository.findByIdAndUserId(sessionId, userId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "Workout session not found")
    }

    private fun WorkoutSet.toResult() = WorkoutSetResult(
        id = id,
        setNumber = setNumber,
        weight = weight,
        reps = reps,
        completed = completed,
        restSeconds = restSeconds,
        completedAt = completedAt,
    )
}

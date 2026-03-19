package com.calaf.overloadmanager.exercise.service

import com.calaf.overloadmanager.common.AppException
import com.calaf.overloadmanager.common.ErrorCode
import com.calaf.overloadmanager.exercise.domain.Exercise
import com.calaf.overloadmanager.exercise.domain.ExerciseCategory
import com.calaf.overloadmanager.exercise.dto.*
import com.calaf.overloadmanager.exercise.repository.ExerciseRepository
import com.calaf.overloadmanager.workout.repository.SessionExerciseRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
@Transactional(readOnly = true)
class ExerciseService(
    private val exerciseRepository: ExerciseRepository,
    private val sessionExerciseRepository: SessionExerciseRepository,
) {

    fun listExercises(
        userId: Long?,
        category: ExerciseCategory?,
        search: String?,
        pageable: Pageable,
    ): Page<ExerciseResponse> {
        val effectiveUserId = userId ?: 0L
        return exerciseRepository.findAllAccessible(effectiveUserId, category, search, pageable)
            .map { it.toResponse() }
    }

    fun getExercise(id: Long): ExerciseResponse {
        val exercise = exerciseRepository.findById(id)
            .orElseThrow { AppException(ErrorCode.NOT_FOUND, "Exercise not found") }
        return exercise.toResponse()
    }

    fun getPreviousSession(
        userId: Long,
        exerciseId: Long,
        excludeSessionId: Long,
    ): PreviousSessionResponse? {
        val sessionExercises = sessionExerciseRepository.findPreviousSessionExercise(
            userId, exerciseId, excludeSessionId
        )
        val sessionExercise = sessionExercises.firstOrNull() ?: return null
        val session = sessionExercise.session

        return PreviousSessionResponse(
            sessionId = session.id,
            sessionDate = session.sessionDate,
            sets = sessionExercise.sets.map {
                PreviousSetResponse(
                    setNumber = it.setNumber,
                    weight = it.weight,
                    reps = it.reps,
                    completed = it.completed,
                )
            },
        )
    }

    fun getExerciseHistory(userId: Long, exerciseId: Long): List<ExerciseHistoryEntry> {
        val sessionExercises = sessionExerciseRepository.findExerciseHistory(userId, exerciseId)

        return sessionExercises.map { se ->
            val session = se.session
            val completedSets = se.sets.filter { it.completed }

            val maxWeightKg = completedSets.maxOfOrNull { it.weight } ?: BigDecimal.ZERO
            val totalVolumeKg = completedSets.fold(BigDecimal.ZERO) { acc, s ->
                acc + s.weight.multiply(BigDecimal(s.reps))
            }

            // Epley formula: weight * (1 + reps/30)
            val bestSet = completedSets.maxByOrNull {
                it.weight.multiply(BigDecimal.ONE + BigDecimal(it.reps).divide(BigDecimal(30), 4, RoundingMode.HALF_UP))
            }
            val estimatedOneRepMax = if (bestSet != null && bestSet.reps > 0) {
                bestSet.weight.multiply(
                    BigDecimal.ONE + BigDecimal(bestSet.reps).divide(BigDecimal(30), 4, RoundingMode.HALF_UP)
                ).setScale(2, RoundingMode.HALF_UP)
            } else BigDecimal.ZERO

            ExerciseHistoryEntry(
                sessionId = session.id,
                sessionDate = session.sessionDate,
                maxWeightKg = maxWeightKg,
                totalVolumeKg = totalVolumeKg.setScale(2, RoundingMode.HALF_UP),
                estimatedOneRepMax = estimatedOneRepMax,
                sets = se.sets.map {
                    PreviousSetResponse(
                        setNumber = it.setNumber,
                        weight = it.weight,
                        reps = it.reps,
                        completed = it.completed,
                    )
                },
            )
        }
    }

    private fun Exercise.toResponse() = ExerciseResponse(
        id = id,
        nameKo = nameKo,
        nameEn = nameEn,
        category = category,
        exerciseType = exerciseType,
        equipment = equipment,
        primaryMuscle = primaryMuscle,
        secondaryMuscles = secondaryMuscles?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
        defaultSetsMin = defaultSetsMin,
        defaultSetsMax = defaultSetsMax,
        defaultRepsMin = defaultRepsMin,
        defaultRepsMax = defaultRepsMax,
        isCustom = isCustom,
    )
}

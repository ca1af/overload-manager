package com.calaf.overloadmanager.exercise.application.service

import com.calaf.overloadmanager.common.AppException
import com.calaf.overloadmanager.common.ErrorCode
import com.calaf.overloadmanager.exercise.domain.model.Exercise
import com.calaf.overloadmanager.exercise.domain.model.ExerciseCategory
import com.calaf.overloadmanager.exercise.domain.port.`in`.*
import com.calaf.overloadmanager.exercise.domain.port.out.ExerciseHistoryRepository
import com.calaf.overloadmanager.exercise.domain.port.out.ExerciseRepository
import com.calaf.overloadmanager.exercise.domain.port.out.SessionExerciseData
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
@Transactional(readOnly = true)
class ExerciseService(
    private val exerciseRepository: ExerciseRepository,
    private val exerciseHistoryRepository: ExerciseHistoryRepository,
) : ListExercisesUseCase, GetExerciseUseCase, GetPreviousSessionUseCase, GetExerciseHistoryUseCase {

    override fun listExercises(
        userId: Long?,
        category: ExerciseCategory?,
        search: String?,
        page: Int,
        size: Int,
    ): PageResult<ExerciseResult> {
        val effectiveUserId = userId ?: 0L
        val pageResult = exerciseRepository.findAllAccessible(effectiveUserId, category, search, page, size)
        return PageResult(
            content = pageResult.content.map { it.toResult() },
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
            number = pageResult.number,
            size = pageResult.size,
            first = pageResult.first,
            last = pageResult.last,
            empty = pageResult.empty,
        )
    }

    override fun getExercise(id: Long): ExerciseResult {
        val exercise = exerciseRepository.findById(id)
            ?: throw AppException(ErrorCode.NOT_FOUND, "Exercise not found")
        return exercise.toResult()
    }

    override fun getPreviousSession(
        userId: Long,
        exerciseId: Long,
        excludeSessionId: Long,
    ): PreviousSessionResult? {
        val sessionExercises = exerciseHistoryRepository.findPreviousSessionExercise(
            userId, exerciseId, excludeSessionId
        )
        val se = sessionExercises.firstOrNull() ?: return null

        return PreviousSessionResult(
            sessionId = se.sessionId,
            sessionDate = se.sessionDate,
            sets = se.sets.map {
                PreviousSetResult(
                    setNumber = it.setNumber,
                    weight = it.weight,
                    reps = it.reps,
                    completed = it.completed,
                )
            },
        )
    }

    override fun getExerciseHistory(userId: Long, exerciseId: Long): List<ExerciseHistoryResult> {
        val sessionExercises = exerciseHistoryRepository.findExerciseHistory(userId, exerciseId)

        return sessionExercises.map { se ->
            val completedSets = se.sets.filter { it.completed }

            val maxWeightKg = completedSets.maxOfOrNull { it.weight } ?: BigDecimal.ZERO
            val totalVolumeKg = completedSets.fold(BigDecimal.ZERO) { acc, s ->
                acc + s.weight.multiply(BigDecimal(s.reps))
            }

            val bestSet = completedSets.maxByOrNull {
                it.weight.multiply(BigDecimal.ONE + BigDecimal(it.reps).divide(BigDecimal(30), 4, RoundingMode.HALF_UP))
            }
            val estimatedOneRepMax = if (bestSet != null && bestSet.reps > 0) {
                bestSet.weight.multiply(
                    BigDecimal.ONE + BigDecimal(bestSet.reps).divide(BigDecimal(30), 4, RoundingMode.HALF_UP)
                ).setScale(2, RoundingMode.HALF_UP)
            } else BigDecimal.ZERO

            ExerciseHistoryResult(
                sessionId = se.sessionId,
                sessionDate = se.sessionDate,
                maxWeightKg = maxWeightKg,
                totalVolumeKg = totalVolumeKg.setScale(2, RoundingMode.HALF_UP),
                estimatedOneRepMax = estimatedOneRepMax,
                sets = se.sets.map {
                    PreviousSetResult(
                        setNumber = it.setNumber,
                        weight = it.weight,
                        reps = it.reps,
                        completed = it.completed,
                    )
                },
            )
        }
    }

    private fun Exercise.toResult() = ExerciseResult(
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

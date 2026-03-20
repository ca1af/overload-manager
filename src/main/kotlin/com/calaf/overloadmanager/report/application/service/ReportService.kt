package com.calaf.overloadmanager.report.application.service

import com.calaf.overloadmanager.report.domain.port.`in`.*
import com.calaf.overloadmanager.workout.domain.port.out.CompletedSetData
import com.calaf.overloadmanager.workout.domain.port.out.WorkoutSessionRepository
import com.calaf.overloadmanager.workout.domain.port.out.WorkoutSetRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Service
@Transactional(readOnly = true)
class ReportService(
    private val sessionRepository: WorkoutSessionRepository,
    private val setRepository: WorkoutSetRepository,
) : GetWeeklySummaryUseCase {

    override fun getWeeklySummary(userId: Long, date: LocalDate?): WeeklySummaryResult {
        val targetDate = date ?: LocalDate.now()
        val weekStart = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weekEnd = weekStart.plusDays(6)

        val sessionCount = sessionRepository.countByUserIdAndDateRange(userId, weekStart, weekEnd)
        val currentSets = setRepository.findCompletedSetsByUserAndDateRange(userId, weekStart, weekEnd)

        // Previous week
        val prevWeekStart = weekStart.minusWeeks(1)
        val prevWeekEnd = prevWeekStart.plusDays(6)
        val previousSets = setRepository.findCompletedSetsByUserAndDateRange(userId, prevWeekStart, prevWeekEnd)

        // Total volume
        val totalVolume = currentSets.fold(BigDecimal.ZERO) { acc, s ->
            acc + s.weight.multiply(BigDecimal(s.reps))
        }.setScale(2, RoundingMode.HALF_UP)

        // Group by exercise
        val currentByExercise = currentSets.groupBy { it.exerciseId }
        val previousByExercise = previousSets.groupBy { it.exerciseId }

        val exerciseSummaries = currentByExercise.map { (exerciseId, sets) ->
            val first = sets.first()
            ExerciseWeeklySummaryResult(
                exerciseId = exerciseId,
                exerciseNameKo = first.exerciseNameKo,
                exerciseNameEn = first.exerciseNameEn,
                totalSets = sets.size,
                totalReps = sets.sumOf { it.reps },
                maxWeight = sets.maxOf { it.weight },
                totalVolume = sets.fold(BigDecimal.ZERO) { acc, s ->
                    acc + s.weight.multiply(BigDecimal(s.reps))
                }.setScale(2, RoundingMode.HALF_UP),
            )
        }

        // Overload achievements
        val overloadAchievements = currentByExercise.mapNotNull { (exerciseId, currentExSets) ->
            val prevSets = previousByExercise[exerciseId]
            if (prevSets != null) {
                val currentMax = currentExSets.maxOf { it.weight }
                val prevMax = prevSets.maxOf { it.weight }
                if (currentMax > prevMax) {
                    val first = currentExSets.first()
                    OverloadAchievementResult(
                        exerciseId = exerciseId,
                        exerciseNameKo = first.exerciseNameKo,
                        exerciseNameEn = first.exerciseNameEn,
                        previousMaxWeight = prevMax,
                        currentMaxWeight = currentMax,
                        improvement = (currentMax - prevMax).setScale(2, RoundingMode.HALF_UP),
                    )
                } else null
            } else null
        }

        return WeeklySummaryResult(
            weekStart = weekStart,
            weekEnd = weekEnd,
            sessionCount = sessionCount,
            totalVolume = totalVolume,
            exerciseSummaries = exerciseSummaries,
            overloadAchievements = overloadAchievements,
        )
    }
}

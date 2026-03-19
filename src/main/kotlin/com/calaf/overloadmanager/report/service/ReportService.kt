package com.calaf.overloadmanager.report.service

import com.calaf.overloadmanager.report.dto.*
import com.calaf.overloadmanager.workout.repository.WorkoutSessionRepository
import com.calaf.overloadmanager.workout.repository.WorkoutSetRepository
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
) {

    fun getWeeklySummary(userId: Long, date: LocalDate?): WeeklySummaryResponse {
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
        val currentByExercise = currentSets.groupBy { it.sessionExercise.exercise }
        val previousByExercise = previousSets.groupBy { it.sessionExercise.exercise }

        val exerciseSummaries = currentByExercise.map { (exercise, sets) ->
            ExerciseWeeklySummary(
                exerciseId = exercise.id,
                exerciseNameKo = exercise.nameKo,
                exerciseNameEn = exercise.nameEn,
                totalSets = sets.size,
                totalReps = sets.sumOf { it.reps },
                maxWeight = sets.maxOf { it.weight },
                totalVolume = sets.fold(BigDecimal.ZERO) { acc, s ->
                    acc + s.weight.multiply(BigDecimal(s.reps))
                }.setScale(2, RoundingMode.HALF_UP),
            )
        }

        // Overload achievements
        val overloadAchievements = currentByExercise.mapNotNull { (exercise, currentExSets) ->
            val prevSets = previousByExercise[exercise]
            if (prevSets != null) {
                val currentMax = currentExSets.maxOf { it.weight }
                val prevMax = prevSets.maxOf { it.weight }
                if (currentMax > prevMax) {
                    OverloadAchievement(
                        exerciseId = exercise.id,
                        exerciseNameKo = exercise.nameKo,
                        exerciseNameEn = exercise.nameEn,
                        previousMaxWeight = prevMax,
                        currentMaxWeight = currentMax,
                        improvement = (currentMax - prevMax).setScale(2, RoundingMode.HALF_UP),
                    )
                } else null
            } else null
        }

        return WeeklySummaryResponse(
            weekStart = weekStart,
            weekEnd = weekEnd,
            sessionCount = sessionCount,
            totalVolume = totalVolume,
            exerciseSummaries = exerciseSummaries,
            overloadAchievements = overloadAchievements,
        )
    }
}

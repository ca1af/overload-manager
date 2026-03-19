package com.calaf.overloadmanager.report.adapter.`in`.web

import com.calaf.overloadmanager.report.domain.port.`in`.*
import java.math.BigDecimal
import java.time.LocalDate

data class WeeklySummaryResponse(
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val sessionCount: Long,
    val totalVolume: BigDecimal,
    val exerciseSummaries: List<ExerciseWeeklySummary>,
    val overloadAchievements: List<OverloadAchievement>,
)

data class ExerciseWeeklySummary(
    val exerciseId: Long,
    val exerciseNameKo: String,
    val exerciseNameEn: String,
    val totalSets: Int,
    val totalReps: Int,
    val maxWeight: BigDecimal,
    val totalVolume: BigDecimal,
)

data class OverloadAchievement(
    val exerciseId: Long,
    val exerciseNameKo: String,
    val exerciseNameEn: String,
    val previousMaxWeight: BigDecimal,
    val currentMaxWeight: BigDecimal,
    val improvement: BigDecimal,
)

fun WeeklySummaryResult.toResponse() = WeeklySummaryResponse(
    weekStart = weekStart,
    weekEnd = weekEnd,
    sessionCount = sessionCount,
    totalVolume = totalVolume,
    exerciseSummaries = exerciseSummaries.map { it.toResponse() },
    overloadAchievements = overloadAchievements.map { it.toResponse() },
)

fun ExerciseWeeklySummaryResult.toResponse() = ExerciseWeeklySummary(
    exerciseId = exerciseId,
    exerciseNameKo = exerciseNameKo,
    exerciseNameEn = exerciseNameEn,
    totalSets = totalSets,
    totalReps = totalReps,
    maxWeight = maxWeight,
    totalVolume = totalVolume,
)

fun OverloadAchievementResult.toResponse() = OverloadAchievement(
    exerciseId = exerciseId,
    exerciseNameKo = exerciseNameKo,
    exerciseNameEn = exerciseNameEn,
    previousMaxWeight = previousMaxWeight,
    currentMaxWeight = currentMaxWeight,
    improvement = improvement,
)

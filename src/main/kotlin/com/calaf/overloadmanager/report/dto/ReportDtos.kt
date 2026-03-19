package com.calaf.overloadmanager.report.dto

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

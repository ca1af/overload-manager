package com.calaf.overloadmanager.report.domain.port.`in`

import java.math.BigDecimal
import java.time.LocalDate

data class WeeklySummaryResult(
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val sessionCount: Long,
    val totalVolume: BigDecimal,
    val exerciseSummaries: List<ExerciseWeeklySummaryResult>,
    val overloadAchievements: List<OverloadAchievementResult>,
)

data class ExerciseWeeklySummaryResult(
    val exerciseId: Long,
    val exerciseNameKo: String,
    val exerciseNameEn: String,
    val totalSets: Int,
    val totalReps: Int,
    val maxWeight: BigDecimal,
    val totalVolume: BigDecimal,
)

data class OverloadAchievementResult(
    val exerciseId: Long,
    val exerciseNameKo: String,
    val exerciseNameEn: String,
    val previousMaxWeight: BigDecimal,
    val currentMaxWeight: BigDecimal,
    val improvement: BigDecimal,
)

interface GetWeeklySummaryUseCase {
    fun getWeeklySummary(userId: Long, date: LocalDate?): WeeklySummaryResult
}

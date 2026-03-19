package com.calaf.overloadmanager.workout.domain.port.out

import com.calaf.overloadmanager.workout.domain.model.WorkoutSet
import java.math.BigDecimal
import java.time.LocalDate

interface WorkoutSetRepository {
    fun findByIdAndSessionExerciseId(id: Long, sessionExerciseId: Long): WorkoutSet?
    fun findMaxSetNumber(sessionExerciseId: Long): Int
    fun save(workoutSet: WorkoutSet): WorkoutSet
    fun delete(workoutSet: WorkoutSet)
    fun findCompletedSetsByUserAndDateRange(userId: Long, startDate: LocalDate, endDate: LocalDate): List<CompletedSetData>
}

data class CompletedSetData(
    val weight: BigDecimal,
    val reps: Int,
    val exerciseId: Long,
    val exerciseNameKo: String,
    val exerciseNameEn: String,
)

package com.calaf.overloadmanager.workout.domain.port.out

import com.calaf.overloadmanager.workout.domain.model.WorkoutSession
import com.calaf.overloadmanager.workout.domain.port.`in`.PageResult
import java.time.LocalDate

interface WorkoutSessionRepository {
    fun findByUserIdWithDateRange(
        userId: Long,
        startDate: LocalDate?,
        endDate: LocalDate?,
        page: Int,
        size: Int,
    ): PageResult<WorkoutSession>

    fun findByIdAndUserId(id: Long, userId: Long): WorkoutSession?
    fun save(session: WorkoutSession): WorkoutSession
    fun delete(session: WorkoutSession)
    fun countByUserIdAndDateRange(userId: Long, startDate: LocalDate, endDate: LocalDate): Long
}

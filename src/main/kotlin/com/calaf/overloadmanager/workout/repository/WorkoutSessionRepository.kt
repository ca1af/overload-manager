package com.calaf.overloadmanager.workout.repository

import com.calaf.overloadmanager.workout.domain.WorkoutSession
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface WorkoutSessionRepository : JpaRepository<WorkoutSession, Long> {

    @Query("""
        SELECT ws FROM WorkoutSession ws
        WHERE ws.user.id = :userId
        AND (:startDate IS NULL OR ws.sessionDate >= :startDate)
        AND (:endDate IS NULL OR ws.sessionDate <= :endDate)
        ORDER BY ws.sessionDate DESC
    """)
    fun findByUserIdWithDateRange(
        userId: Long,
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageable: Pageable,
    ): Page<WorkoutSession>

    fun findByIdAndUserId(id: Long, userId: Long): WorkoutSession?

    @Query("""
        SELECT COUNT(ws) FROM WorkoutSession ws
        WHERE ws.user.id = :userId
        AND ws.sessionDate >= :startDate
        AND ws.sessionDate <= :endDate
    """)
    fun countByUserIdAndDateRange(userId: Long, startDate: LocalDate, endDate: LocalDate): Long
}

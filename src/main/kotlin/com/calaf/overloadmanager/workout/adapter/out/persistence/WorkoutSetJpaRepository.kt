package com.calaf.overloadmanager.workout.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface WorkoutSetJpaRepository : JpaRepository<WorkoutSetJpaEntity, Long> {

    fun findByIdAndSessionExerciseId(id: Long, sessionExerciseId: Long): WorkoutSetJpaEntity?

    @Query("SELECT COALESCE(MAX(ws.setNumber), 0) FROM WorkoutSetJpaEntity ws WHERE ws.sessionExercise.id = :sessionExerciseId")
    fun findMaxSetNumber(sessionExerciseId: Long): Int

    @Query("""
        SELECT ws FROM WorkoutSetJpaEntity ws
        JOIN ws.sessionExercise se
        JOIN se.session s
        WHERE s.user.id = :userId
        AND s.sessionDate >= :startDate
        AND s.sessionDate <= :endDate
        AND ws.completed = true
    """)
    fun findCompletedSetsByUserAndDateRange(userId: Long, startDate: LocalDate, endDate: LocalDate): List<WorkoutSetJpaEntity>
}

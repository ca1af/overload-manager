package com.calaf.overloadmanager.workout.repository

import com.calaf.overloadmanager.workout.domain.WorkoutSet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface WorkoutSetRepository : JpaRepository<WorkoutSet, Long> {

    fun findByIdAndSessionExerciseId(id: Long, sessionExerciseId: Long): WorkoutSet?

    @Query("SELECT COALESCE(MAX(ws.setNumber), 0) FROM WorkoutSet ws WHERE ws.sessionExercise.id = :sessionExerciseId")
    fun findMaxSetNumber(sessionExerciseId: Long): Int

    @Query("""
        SELECT ws FROM WorkoutSet ws
        JOIN ws.sessionExercise se
        JOIN se.session s
        WHERE s.user.id = :userId
        AND s.sessionDate >= :startDate
        AND s.sessionDate <= :endDate
        AND ws.completed = true
    """)
    fun findCompletedSetsByUserAndDateRange(userId: Long, startDate: LocalDate, endDate: LocalDate): List<WorkoutSet>
}

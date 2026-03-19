package com.calaf.overloadmanager.workout.repository

import com.calaf.overloadmanager.workout.domain.SessionExercise
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SessionExerciseRepository : JpaRepository<SessionExercise, Long> {

    fun findBySessionId(sessionId: Long): List<SessionExercise>

    @Query("""
        SELECT se FROM SessionExercise se
        JOIN FETCH se.sets
        JOIN se.session ws
        WHERE ws.user.id = :userId
        AND se.exercise.id = :exerciseId
        AND ws.id != :excludeSessionId
        ORDER BY ws.sessionDate DESC
    """)
    fun findPreviousSessionExercise(
        userId: Long,
        exerciseId: Long,
        excludeSessionId: Long,
    ): List<SessionExercise>

    @Query("""
        SELECT se FROM SessionExercise se
        JOIN FETCH se.sets s
        JOIN se.session ws
        WHERE ws.user.id = :userId
        AND se.exercise.id = :exerciseId
        ORDER BY ws.sessionDate DESC
    """)
    fun findExerciseHistory(userId: Long, exerciseId: Long): List<SessionExercise>

    fun findByIdAndSessionId(id: Long, sessionId: Long): SessionExercise?

    @Query("SELECT COALESCE(MAX(se.orderIndex), -1) FROM SessionExercise se WHERE se.session.id = :sessionId")
    fun findMaxOrderIndex(sessionId: Long): Int
}

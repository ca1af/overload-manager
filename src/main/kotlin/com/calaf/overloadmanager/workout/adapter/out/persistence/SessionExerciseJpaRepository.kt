package com.calaf.overloadmanager.workout.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SessionExerciseJpaRepository : JpaRepository<SessionExerciseJpaEntity, Long> {

    fun findBySessionId(sessionId: Long): List<SessionExerciseJpaEntity>

    @Query("""
        SELECT se FROM SessionExerciseJpaEntity se
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
    ): List<SessionExerciseJpaEntity>

    @Query("""
        SELECT se FROM SessionExerciseJpaEntity se
        JOIN FETCH se.sets s
        JOIN se.session ws
        WHERE ws.user.id = :userId
        AND se.exercise.id = :exerciseId
        ORDER BY ws.sessionDate DESC
    """)
    fun findExerciseHistory(userId: Long, exerciseId: Long): List<SessionExerciseJpaEntity>

    fun findByIdAndSessionId(id: Long, sessionId: Long): SessionExerciseJpaEntity?

    @Query("SELECT COALESCE(MAX(se.orderIndex), -1) FROM SessionExerciseJpaEntity se WHERE se.session.id = :sessionId")
    fun findMaxOrderIndex(sessionId: Long): Int
}

package com.calaf.overloadmanager.workout.domain.port.out

import com.calaf.overloadmanager.workout.domain.model.SessionExercise

interface SessionExerciseRepository {
    fun findByIdAndSessionId(id: Long, sessionId: Long): SessionExercise?
    fun findMaxOrderIndex(sessionId: Long): Int
    fun save(sessionExercise: SessionExercise): SessionExercise
    fun delete(sessionExercise: SessionExercise)
}

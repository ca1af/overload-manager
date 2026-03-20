package com.calaf.overloadmanager.workout.adapter.out.persistence

import com.calaf.overloadmanager.exercise.adapter.out.persistence.ExerciseJpaRepository
import com.calaf.overloadmanager.workout.domain.model.SessionExercise
import com.calaf.overloadmanager.workout.domain.port.out.SessionExerciseRepository
import org.springframework.stereotype.Repository

@Repository
class SessionExercisePersistenceAdapter(
    private val jpaRepository: SessionExerciseJpaRepository,
    private val sessionJpaRepository: WorkoutSessionJpaRepository,
    private val exerciseJpaRepository: ExerciseJpaRepository,
) : SessionExerciseRepository {

    override fun findByIdAndSessionId(id: Long, sessionId: Long): SessionExercise? =
        jpaRepository.findByIdAndSessionId(id, sessionId)?.toDomain()

    override fun findMaxOrderIndex(sessionId: Long): Int =
        jpaRepository.findMaxOrderIndex(sessionId)

    override fun save(sessionExercise: SessionExercise): SessionExercise {
        val sessionEntity = sessionJpaRepository.findById(sessionExercise.sessionId).orElseThrow()
        val exerciseEntity = exerciseJpaRepository.findById(sessionExercise.exerciseId).orElseThrow()
        val entity = if (sessionExercise.id == 0L) {
            SessionExerciseJpaEntity(
                session = sessionEntity,
                exercise = exerciseEntity,
                orderIndex = sessionExercise.orderIndex,
            )
        } else {
            val existing = jpaRepository.findById(sessionExercise.id).orElseThrow()
            existing.orderIndex = sessionExercise.orderIndex
            existing
        }
        return jpaRepository.save(entity).toDomain()
    }

    override fun delete(sessionExercise: SessionExercise) {
        jpaRepository.deleteById(sessionExercise.id)
    }
}

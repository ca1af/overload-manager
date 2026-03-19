package com.calaf.overloadmanager.workout.adapter.out.persistence

import com.calaf.overloadmanager.workout.domain.model.WorkoutSet
import com.calaf.overloadmanager.workout.domain.port.out.CompletedSetData
import com.calaf.overloadmanager.workout.domain.port.out.WorkoutSetRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class WorkoutSetPersistenceAdapter(
    private val jpaRepository: WorkoutSetJpaRepository,
    private val sessionExerciseJpaRepository: SessionExerciseJpaRepository,
) : WorkoutSetRepository {

    override fun findByIdAndSessionExerciseId(id: Long, sessionExerciseId: Long): WorkoutSet? =
        jpaRepository.findByIdAndSessionExerciseId(id, sessionExerciseId)?.toDomain()

    override fun findMaxSetNumber(sessionExerciseId: Long): Int =
        jpaRepository.findMaxSetNumber(sessionExerciseId)

    override fun save(workoutSet: WorkoutSet): WorkoutSet {
        val entity = if (workoutSet.id == 0L) {
            val seEntity = sessionExerciseJpaRepository.findById(workoutSet.sessionExerciseId).orElseThrow()
            WorkoutSetJpaEntity(
                sessionExercise = seEntity,
                setNumber = workoutSet.setNumber,
                weight = workoutSet.weight,
                reps = workoutSet.reps,
                completed = workoutSet.completed,
                restSeconds = workoutSet.restSeconds,
                completedAt = workoutSet.completedAt,
            )
        } else {
            val existing = jpaRepository.findById(workoutSet.id).orElseThrow()
            existing.setNumber = workoutSet.setNumber
            existing.weight = workoutSet.weight
            existing.reps = workoutSet.reps
            existing.completed = workoutSet.completed
            existing.restSeconds = workoutSet.restSeconds
            existing.completedAt = workoutSet.completedAt
            existing
        }
        return jpaRepository.save(entity).toDomain()
    }

    override fun delete(workoutSet: WorkoutSet) {
        jpaRepository.deleteById(workoutSet.id)
    }

    override fun findCompletedSetsByUserAndDateRange(
        userId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<CompletedSetData> {
        return jpaRepository.findCompletedSetsByUserAndDateRange(userId, startDate, endDate).map { ws ->
            val exercise = ws.sessionExercise.exercise
            CompletedSetData(
                weight = ws.weight,
                reps = ws.reps,
                exerciseId = exercise.id,
                exerciseNameKo = exercise.nameKo,
                exerciseNameEn = exercise.nameEn,
            )
        }
    }
}

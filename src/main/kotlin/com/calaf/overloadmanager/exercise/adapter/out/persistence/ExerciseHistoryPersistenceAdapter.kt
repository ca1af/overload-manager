package com.calaf.overloadmanager.exercise.adapter.out.persistence

import com.calaf.overloadmanager.exercise.domain.port.out.ExerciseHistoryRepository
import com.calaf.overloadmanager.exercise.domain.port.out.SessionExerciseData
import com.calaf.overloadmanager.exercise.domain.port.out.SetData
import com.calaf.overloadmanager.workout.adapter.out.persistence.SessionExerciseJpaRepository
import org.springframework.stereotype.Repository

@Repository
class ExerciseHistoryPersistenceAdapter(
    private val sessionExerciseJpaRepository: SessionExerciseJpaRepository,
) : ExerciseHistoryRepository {

    override fun findPreviousSessionExercise(
        userId: Long,
        exerciseId: Long,
        excludeSessionId: Long,
    ): List<SessionExerciseData> {
        return sessionExerciseJpaRepository.findPreviousSessionExercise(userId, exerciseId, excludeSessionId)
            .map { se ->
                SessionExerciseData(
                    sessionExerciseId = se.id,
                    sessionId = se.session.id,
                    sessionDate = se.session.sessionDate,
                    sets = se.sets.map { s ->
                        SetData(
                            setNumber = s.setNumber,
                            weight = s.weight,
                            reps = s.reps,
                            completed = s.completed,
                        )
                    },
                )
            }
    }

    override fun findExerciseHistory(
        userId: Long,
        exerciseId: Long,
    ): List<SessionExerciseData> {
        return sessionExerciseJpaRepository.findExerciseHistory(userId, exerciseId)
            .map { se ->
                SessionExerciseData(
                    sessionExerciseId = se.id,
                    sessionId = se.session.id,
                    sessionDate = se.session.sessionDate,
                    sets = se.sets.map { s ->
                        SetData(
                            setNumber = s.setNumber,
                            weight = s.weight,
                            reps = s.reps,
                            completed = s.completed,
                        )
                    },
                )
            }
    }
}

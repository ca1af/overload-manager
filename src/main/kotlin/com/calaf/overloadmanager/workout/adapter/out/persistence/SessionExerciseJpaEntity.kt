package com.calaf.overloadmanager.workout.adapter.out.persistence

import com.calaf.overloadmanager.exercise.adapter.out.persistence.ExerciseJpaEntity
import com.calaf.overloadmanager.workout.domain.model.SessionExercise
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "session_exercises")
@EntityListeners(AuditingEntityListener::class)
class SessionExerciseJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    val session: WorkoutSessionJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    val exercise: ExerciseJpaEntity,

    @Column(name = "order_index", nullable = false)
    var orderIndex: Int,

    @OneToMany(mappedBy = "sessionExercise", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("setNumber ASC")
    val sets: MutableList<WorkoutSetJpaEntity> = mutableListOf(),

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
)

fun SessionExerciseJpaEntity.toDomain() = SessionExercise(
    id = id,
    sessionId = session.id,
    exerciseId = exercise.id,
    exerciseNameKo = exercise.nameKo,
    exerciseNameEn = exercise.nameEn,
    exerciseCategory = exercise.category.name,
    orderIndex = orderIndex,
    sets = sets.map { it.toDomain() },
    createdAt = createdAt,
)

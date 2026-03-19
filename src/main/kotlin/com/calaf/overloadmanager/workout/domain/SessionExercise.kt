package com.calaf.overloadmanager.workout.domain

import com.calaf.overloadmanager.exercise.domain.Exercise
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "session_exercises")
@EntityListeners(AuditingEntityListener::class)
class SessionExercise(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    val session: WorkoutSession,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    val exercise: Exercise,

    @Column(name = "order_index", nullable = false)
    var orderIndex: Int,

    @OneToMany(mappedBy = "sessionExercise", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("setNumber ASC")
    val sets: MutableList<WorkoutSet> = mutableListOf(),

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
)

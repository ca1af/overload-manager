package com.calaf.overloadmanager.exercise.domain

import com.calaf.overloadmanager.user.domain.User
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "exercises")
@EntityListeners(AuditingEntityListener::class)
class Exercise(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    val createdBy: User? = null,

    @Column(name = "name_ko", nullable = false, length = 100)
    val nameKo: String,

    @Column(name = "name_en", nullable = false, length = 100)
    val nameEn: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val category: ExerciseCategory,

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_type", nullable = false, length = 20)
    val exerciseType: ExerciseType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val equipment: Equipment,

    @Column(name = "primary_muscle", nullable = false, length = 50)
    val primaryMuscle: String,

    @Column(name = "secondary_muscles", length = 500)
    val secondaryMuscles: String? = null,

    @Column(name = "default_sets_min", nullable = false)
    val defaultSetsMin: Int = 3,

    @Column(name = "default_sets_max", nullable = false)
    val defaultSetsMax: Int = 5,

    @Column(name = "default_reps_min", nullable = false)
    val defaultRepsMin: Int = 8,

    @Column(name = "default_reps_max", nullable = false)
    val defaultRepsMax: Int = 12,

    @Column(name = "is_custom", nullable = false)
    val isCustom: Boolean = false,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
)

enum class ExerciseCategory {
    CHEST, BACK, LEGS, SHOULDERS, BICEPS, TRICEPS, CORE
}

enum class ExerciseType {
    COMPOUND, ISOLATION
}

enum class Equipment {
    BARBELL, DUMBBELL, MACHINE, CABLE, BODYWEIGHT
}

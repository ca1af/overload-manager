package com.calaf.overloadmanager.exercise.domain.model

import java.time.LocalDateTime

data class Exercise(
    val id: Long = 0,
    val createdByUserId: Long? = null,
    val nameKo: String,
    val nameEn: String,
    val category: ExerciseCategory,
    val exerciseType: ExerciseType,
    val equipment: Equipment,
    val primaryMuscle: String,
    val secondaryMuscles: String? = null,
    val defaultSetsMin: Int = 3,
    val defaultSetsMax: Int = 5,
    val defaultRepsMin: Int = 8,
    val defaultRepsMax: Int = 12,
    val isCustom: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
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

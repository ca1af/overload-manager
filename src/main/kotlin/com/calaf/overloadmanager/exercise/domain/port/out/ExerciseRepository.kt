package com.calaf.overloadmanager.exercise.domain.port.out

import com.calaf.overloadmanager.exercise.domain.model.Exercise
import com.calaf.overloadmanager.exercise.domain.model.ExerciseCategory
import com.calaf.overloadmanager.exercise.domain.port.`in`.PageResult

interface ExerciseRepository {
    fun findAllAccessible(
        userId: Long,
        category: ExerciseCategory?,
        search: String?,
        page: Int,
        size: Int,
    ): PageResult<Exercise>

    fun findById(id: Long): Exercise?
}

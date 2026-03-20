package com.calaf.overloadmanager.exercise.adapter.out.persistence

import com.calaf.overloadmanager.exercise.domain.model.Exercise
import com.calaf.overloadmanager.exercise.domain.model.ExerciseCategory
import com.calaf.overloadmanager.exercise.domain.port.`in`.PageResult
import com.calaf.overloadmanager.exercise.domain.port.out.ExerciseRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class ExercisePersistenceAdapter(
    private val jpaRepository: ExerciseJpaRepository,
) : ExerciseRepository {

    override fun findAllAccessible(
        userId: Long,
        category: ExerciseCategory?,
        search: String?,
        page: Int,
        size: Int,
    ): PageResult<Exercise> {
        val springPage = jpaRepository.findAllAccessible(userId, category, search, PageRequest.of(page, size))
        return PageResult(
            content = springPage.content.map { it.toDomain() },
            totalElements = springPage.totalElements,
            totalPages = springPage.totalPages,
            number = springPage.number,
            size = springPage.size,
            first = springPage.isFirst,
            last = springPage.isLast,
            empty = springPage.isEmpty,
        )
    }

    override fun findById(id: Long): Exercise? =
        jpaRepository.findById(id).orElse(null)?.toDomain()
}

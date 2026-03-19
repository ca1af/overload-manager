package com.calaf.overloadmanager.exercise.adapter.out.persistence

import com.calaf.overloadmanager.exercise.domain.model.ExerciseCategory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ExerciseJpaRepository : JpaRepository<ExerciseJpaEntity, Long> {

    @Query("""
        SELECT e FROM ExerciseJpaEntity e
        WHERE (e.isCustom = false OR e.createdBy.id = :userId)
        AND (:category IS NULL OR e.category = :category)
        AND (:search IS NULL OR LOWER(e.nameKo) LIKE LOWER(CONCAT('%', :search, '%'))
             OR LOWER(e.nameEn) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    fun findAllAccessible(
        userId: Long,
        category: ExerciseCategory?,
        search: String?,
        pageable: Pageable,
    ): Page<ExerciseJpaEntity>
}

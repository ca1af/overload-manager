package com.calaf.overloadmanager.workout.adapter.out.persistence

import com.calaf.overloadmanager.user.adapter.out.persistence.UserJpaRepository
import com.calaf.overloadmanager.workout.domain.model.WorkoutSession
import com.calaf.overloadmanager.workout.domain.port.`in`.PageResult
import com.calaf.overloadmanager.workout.domain.port.out.WorkoutSessionRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class WorkoutSessionPersistenceAdapter(
    private val jpaRepository: WorkoutSessionJpaRepository,
    private val userJpaRepository: UserJpaRepository,
) : WorkoutSessionRepository {

    override fun findByUserIdWithDateRange(
        userId: Long,
        startDate: LocalDate?,
        endDate: LocalDate?,
        page: Int,
        size: Int,
    ): PageResult<WorkoutSession> {
        val springPage = jpaRepository.findByUserIdWithDateRange(userId, startDate, endDate, PageRequest.of(page, size))
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

    override fun findByIdAndUserId(id: Long, userId: Long): WorkoutSession? =
        jpaRepository.findByIdAndUserId(id, userId)?.toDomain()

    override fun save(session: WorkoutSession): WorkoutSession {
        val userEntity = userJpaRepository.findById(session.userId).orElseThrow()
        val entity = if (session.id == 0L) {
            WorkoutSessionJpaEntity(
                user = userEntity,
                sessionDate = session.sessionDate,
                notes = session.notes,
                startedAt = session.startedAt,
                finishedAt = session.finishedAt,
                durationSeconds = session.durationSeconds,
            )
        } else {
            val existing = jpaRepository.findById(session.id).orElseThrow()
            existing.sessionDate = session.sessionDate
            existing.notes = session.notes
            existing.startedAt = session.startedAt
            existing.finishedAt = session.finishedAt
            existing.durationSeconds = session.durationSeconds
            existing
        }
        return jpaRepository.save(entity).toDomain()
    }

    override fun delete(session: WorkoutSession) {
        jpaRepository.deleteById(session.id)
    }

    override fun countByUserIdAndDateRange(userId: Long, startDate: LocalDate, endDate: LocalDate): Long =
        jpaRepository.countByUserIdAndDateRange(userId, startDate, endDate)
}

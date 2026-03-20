package com.calaf.overloadmanager.workout.adapter.`in`.web

import com.calaf.overloadmanager.common.ApiResponse
import com.calaf.overloadmanager.workout.domain.port.`in`.*
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/sessions")
class WorkoutSessionController(
    private val listSessionsUseCase: ListSessionsUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val updateSessionUseCase: UpdateSessionUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase,
    private val addExercisesToSessionUseCase: AddExercisesToSessionUseCase,
    private val removeExerciseFromSessionUseCase: RemoveExerciseFromSessionUseCase,
) {

    @GetMapping
    fun listSessions(
        auth: Authentication,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ResponseEntity<ApiResponse<Page<SessionSummaryResponse>>> {
        val userId = auth.principal as Long
        val result = listSessionsUseCase.listSessions(userId, startDate, endDate, pageable.pageNumber, pageable.pageSize)
        val page: Page<SessionSummaryResponse> = PageImpl(
            result.content.map { it.toResponse() },
            pageable,
            result.totalElements,
        )
        return ResponseEntity.ok(ApiResponse.success(page))
    }

    @PostMapping
    fun createSession(
        auth: Authentication,
        @Valid @RequestBody request: CreateSessionRequest,
    ): ResponseEntity<ApiResponse<SessionDetailResponse>> {
        val userId = auth.principal as Long
        val result = createSessionUseCase.createSession(userId, request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result.toResponse()))
    }

    @GetMapping("/{id}")
    fun getSession(
        auth: Authentication,
        @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<SessionDetailResponse>> {
        val userId = auth.principal as Long
        val result = getSessionUseCase.getSession(userId, id)
        return ResponseEntity.ok(ApiResponse.success(result.toResponse()))
    }

    @PatchMapping("/{id}")
    fun updateSession(
        auth: Authentication,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateSessionRequest,
    ): ResponseEntity<ApiResponse<SessionDetailResponse>> {
        val userId = auth.principal as Long
        val result = updateSessionUseCase.updateSession(userId, id, request.toCommand())
        return ResponseEntity.ok(ApiResponse.success(result.toResponse()))
    }

    @DeleteMapping("/{id}")
    fun deleteSession(
        auth: Authentication,
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        val userId = auth.principal as Long
        deleteSessionUseCase.deleteSession(userId, id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/exercises")
    fun addExercises(
        auth: Authentication,
        @PathVariable id: Long,
        @Valid @RequestBody request: AddExercisesRequest,
    ): ResponseEntity<ApiResponse<List<AddExerciseResponse>>> {
        val userId = auth.principal as Long
        val result = addExercisesToSessionUseCase.addExercises(userId, id, request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result.map { it.toResponse() }))
    }

    @DeleteMapping("/{id}/exercises/{sessionExerciseId}")
    fun removeExercise(
        auth: Authentication,
        @PathVariable id: Long,
        @PathVariable sessionExerciseId: Long,
    ): ResponseEntity<Void> {
        val userId = auth.principal as Long
        removeExerciseFromSessionUseCase.removeExercise(userId, id, sessionExerciseId)
        return ResponseEntity.noContent().build()
    }
}

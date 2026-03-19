package com.calaf.overloadmanager.workout.controller

import com.calaf.overloadmanager.common.ApiResponse
import com.calaf.overloadmanager.workout.dto.*
import com.calaf.overloadmanager.workout.service.WorkoutSessionService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
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
    private val sessionService: WorkoutSessionService,
) {

    @GetMapping
    fun listSessions(
        auth: Authentication,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ResponseEntity<ApiResponse<Page<SessionSummaryResponse>>> {
        val userId = auth.principal as Long
        return ResponseEntity.ok(
            ApiResponse.success(sessionService.listSessions(userId, startDate, endDate, pageable))
        )
    }

    @PostMapping
    fun createSession(
        auth: Authentication,
        @Valid @RequestBody request: CreateSessionRequest,
    ): ResponseEntity<ApiResponse<SessionDetailResponse>> {
        val userId = auth.principal as Long
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(sessionService.createSession(userId, request)))
    }

    @GetMapping("/{id}")
    fun getSession(
        auth: Authentication,
        @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<SessionDetailResponse>> {
        val userId = auth.principal as Long
        return ResponseEntity.ok(ApiResponse.success(sessionService.getSession(userId, id)))
    }

    @PatchMapping("/{id}")
    fun updateSession(
        auth: Authentication,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateSessionRequest,
    ): ResponseEntity<ApiResponse<SessionDetailResponse>> {
        val userId = auth.principal as Long
        return ResponseEntity.ok(ApiResponse.success(sessionService.updateSession(userId, id, request)))
    }

    @DeleteMapping("/{id}")
    fun deleteSession(
        auth: Authentication,
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        val userId = auth.principal as Long
        sessionService.deleteSession(userId, id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/exercises")
    fun addExercises(
        auth: Authentication,
        @PathVariable id: Long,
        @Valid @RequestBody request: AddExercisesRequest,
    ): ResponseEntity<ApiResponse<List<AddExerciseResponse>>> {
        val userId = auth.principal as Long
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(sessionService.addExercises(userId, id, request)))
    }

    @DeleteMapping("/{id}/exercises/{sessionExerciseId}")
    fun removeExercise(
        auth: Authentication,
        @PathVariable id: Long,
        @PathVariable sessionExerciseId: Long,
    ): ResponseEntity<Void> {
        val userId = auth.principal as Long
        sessionService.removeExercise(userId, id, sessionExerciseId)
        return ResponseEntity.noContent().build()
    }
}

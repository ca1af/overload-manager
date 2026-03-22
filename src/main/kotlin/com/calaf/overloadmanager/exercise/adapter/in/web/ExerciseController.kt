package com.calaf.overloadmanager.exercise.adapter.`in`.web

import com.calaf.overloadmanager.common.ApiResponse
import com.calaf.overloadmanager.common.PageResponse
import com.calaf.overloadmanager.exercise.domain.model.ExerciseCategory
import com.calaf.overloadmanager.exercise.domain.port.`in`.*
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/exercises")
class ExerciseController(
    private val listExercisesUseCase: ListExercisesUseCase,
    private val getExerciseUseCase: GetExerciseUseCase,
    private val getPreviousSessionUseCase: GetPreviousSessionUseCase,
    private val getExerciseHistoryUseCase: GetExerciseHistoryUseCase,
    private val createExerciseUseCase: CreateExerciseUseCase,
) {

    @PostMapping
    fun createExercise(
        auth: Authentication,
        @Valid @RequestBody request: CreateExerciseRequest,
    ): ResponseEntity<ApiResponse<ExerciseResponse>> {
        val userId = auth.principal as Long
        val result = createExerciseUseCase.createExercise(userId, request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result.toResponse()))
    }

    @GetMapping
    fun listExercises(
        @RequestParam(required = false) category: ExerciseCategory?,
        @RequestParam(required = false) search: String?,
        @PageableDefault(size = 20) pageable: Pageable,
        auth: Authentication?,
    ): ResponseEntity<ApiResponse<PageResponse<ExerciseResponse>>> {
        val userId = auth?.principal as? Long
        val result = listExercisesUseCase.listExercises(userId, category, search, pageable.pageNumber, pageable.pageSize)
        val page = PageResponse(
            content = result.content.map { it.toResponse() },
            totalElements = result.totalElements,
            totalPages = result.totalPages,
            number = result.number,
            size = result.size,
            first = result.first,
            last = result.last,
            empty = result.empty,
        )
        return ResponseEntity.ok(ApiResponse.success(page))
    }

    @GetMapping("/{id}")
    fun getExercise(@PathVariable id: Long): ResponseEntity<ApiResponse<ExerciseResponse>> {
        val result = getExerciseUseCase.getExercise(id)
        return ResponseEntity.ok(ApiResponse.success(result.toResponse()))
    }

    @GetMapping("/{exerciseId}/previous-session")
    fun getPreviousSession(
        auth: Authentication,
        @PathVariable exerciseId: Long,
        @RequestParam(required = false, defaultValue = "0") excludeSessionId: Long,
    ): ResponseEntity<ApiResponse<PreviousSessionResponse?>> {
        val userId = auth.principal as Long
        val result = getPreviousSessionUseCase.getPreviousSession(userId, exerciseId, excludeSessionId)
        return ResponseEntity.ok(ApiResponse.success(result?.toResponse()))
    }

    @GetMapping("/{exerciseId}/history")
    fun getExerciseHistory(
        auth: Authentication,
        @PathVariable exerciseId: Long,
    ): ResponseEntity<ApiResponse<List<ExerciseHistoryResponse>>> {
        val userId = auth.principal as Long
        val result = getExerciseHistoryUseCase.getExerciseHistory(userId, exerciseId)
        return ResponseEntity.ok(ApiResponse.success(result.map { it.toResponse() }))
    }
}

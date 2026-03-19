package com.calaf.overloadmanager.exercise.controller

import com.calaf.overloadmanager.common.ApiResponse
import com.calaf.overloadmanager.exercise.domain.ExerciseCategory
import com.calaf.overloadmanager.exercise.dto.ExerciseHistoryEntry
import com.calaf.overloadmanager.exercise.dto.ExerciseResponse
import com.calaf.overloadmanager.exercise.dto.PreviousSessionResponse
import com.calaf.overloadmanager.exercise.service.ExerciseService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/exercises")
class ExerciseController(
    private val exerciseService: ExerciseService,
) {

    @GetMapping
    fun listExercises(
        @RequestParam(required = false) category: ExerciseCategory?,
        @RequestParam(required = false) search: String?,
        @PageableDefault(size = 20) pageable: Pageable,
        auth: Authentication?,
    ): ResponseEntity<ApiResponse<Page<ExerciseResponse>>> {
        val userId = auth?.principal as? Long
        return ResponseEntity.ok(
            ApiResponse.success(exerciseService.listExercises(userId, category, search, pageable))
        )
    }

    @GetMapping("/{id}")
    fun getExercise(@PathVariable id: Long): ResponseEntity<ApiResponse<ExerciseResponse>> {
        return ResponseEntity.ok(ApiResponse.success(exerciseService.getExercise(id)))
    }

    @GetMapping("/{exerciseId}/previous-session")
    fun getPreviousSession(
        auth: Authentication,
        @PathVariable exerciseId: Long,
        @RequestParam(required = false, defaultValue = "0") excludeSessionId: Long,
    ): ResponseEntity<ApiResponse<PreviousSessionResponse?>> {
        val userId = auth.principal as Long
        return ResponseEntity.ok(
            ApiResponse.success(exerciseService.getPreviousSession(userId, exerciseId, excludeSessionId))
        )
    }

    @GetMapping("/{exerciseId}/history")
    fun getExerciseHistory(
        auth: Authentication,
        @PathVariable exerciseId: Long,
    ): ResponseEntity<ApiResponse<List<ExerciseHistoryEntry>>> {
        val userId = auth.principal as Long
        return ResponseEntity.ok(
            ApiResponse.success(exerciseService.getExerciseHistory(userId, exerciseId))
        )
    }
}

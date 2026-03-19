package com.calaf.overloadmanager.workout.controller

import com.calaf.overloadmanager.common.ApiResponse
import com.calaf.overloadmanager.workout.dto.*
import com.calaf.overloadmanager.workout.service.WorkoutSetService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/sessions/{sessionId}/exercises/{sessionExerciseId}/sets")
class WorkoutSetController(
    private val setService: WorkoutSetService,
) {

    @PostMapping
    fun createSet(
        auth: Authentication,
        @PathVariable sessionId: Long,
        @PathVariable sessionExerciseId: Long,
        @Valid @RequestBody request: CreateSetRequest,
    ): ResponseEntity<ApiResponse<WorkoutSetResponse>> {
        val userId = auth.principal as Long
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(setService.createSet(userId, sessionId, sessionExerciseId, request)))
    }

    @PatchMapping("/{setId}")
    fun updateSet(
        auth: Authentication,
        @PathVariable sessionId: Long,
        @PathVariable sessionExerciseId: Long,
        @PathVariable setId: Long,
        @Valid @RequestBody request: UpdateSetRequest,
    ): ResponseEntity<ApiResponse<WorkoutSetResponse>> {
        val userId = auth.principal as Long
        return ResponseEntity.ok(
            ApiResponse.success(setService.updateSet(userId, sessionId, sessionExerciseId, setId, request))
        )
    }

    @DeleteMapping("/{setId}")
    fun deleteSet(
        auth: Authentication,
        @PathVariable sessionId: Long,
        @PathVariable sessionExerciseId: Long,
        @PathVariable setId: Long,
    ): ResponseEntity<Void> {
        val userId = auth.principal as Long
        setService.deleteSet(userId, sessionId, sessionExerciseId, setId)
        return ResponseEntity.noContent().build()
    }
}

package com.calaf.overloadmanager.workout.adapter.`in`.web

import com.calaf.overloadmanager.common.ApiResponse
import com.calaf.overloadmanager.workout.domain.port.`in`.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/sessions/{sessionId}/exercises/{sessionExerciseId}/sets")
class WorkoutSetController(
    private val createSetUseCase: CreateSetUseCase,
    private val updateSetUseCase: UpdateSetUseCase,
    private val deleteSetUseCase: DeleteSetUseCase,
) {

    @PostMapping
    fun createSet(
        auth: Authentication,
        @PathVariable sessionId: Long,
        @PathVariable sessionExerciseId: Long,
        @Valid @RequestBody request: CreateSetRequest,
    ): ResponseEntity<ApiResponse<WorkoutSetResponse>> {
        val userId = auth.principal as Long
        val result = createSetUseCase.createSet(userId, sessionId, sessionExerciseId, request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result.toResponse()))
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
        val result = updateSetUseCase.updateSet(userId, sessionId, sessionExerciseId, setId, request.toCommand())
        return ResponseEntity.ok(ApiResponse.success(result.toResponse()))
    }

    @DeleteMapping("/{setId}")
    fun deleteSet(
        auth: Authentication,
        @PathVariable sessionId: Long,
        @PathVariable sessionExerciseId: Long,
        @PathVariable setId: Long,
    ): ResponseEntity<Void> {
        val userId = auth.principal as Long
        deleteSetUseCase.deleteSet(userId, sessionId, sessionExerciseId, setId)
        return ResponseEntity.noContent().build()
    }
}

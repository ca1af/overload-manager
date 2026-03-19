package com.calaf.overloadmanager.report.adapter.`in`.web

import com.calaf.overloadmanager.common.ApiResponse
import com.calaf.overloadmanager.report.domain.port.`in`.GetWeeklySummaryUseCase
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/reports")
class ReportController(
    private val getWeeklySummaryUseCase: GetWeeklySummaryUseCase,
) {

    @GetMapping("/weekly-summary")
    fun getWeeklySummary(
        auth: Authentication,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?,
    ): ResponseEntity<ApiResponse<WeeklySummaryResponse>> {
        val userId = auth.principal as Long
        val result = getWeeklySummaryUseCase.getWeeklySummary(userId, date)
        return ResponseEntity.ok(ApiResponse.success(result.toResponse()))
    }
}

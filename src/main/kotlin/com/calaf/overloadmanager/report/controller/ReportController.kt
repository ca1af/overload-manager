package com.calaf.overloadmanager.report.controller

import com.calaf.overloadmanager.common.ApiResponse
import com.calaf.overloadmanager.report.dto.WeeklySummaryResponse
import com.calaf.overloadmanager.report.service.ReportService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/reports")
class ReportController(
    private val reportService: ReportService,
) {

    @GetMapping("/weekly-summary")
    fun getWeeklySummary(
        auth: Authentication,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?,
    ): ResponseEntity<ApiResponse<WeeklySummaryResponse>> {
        val userId = auth.principal as Long
        return ResponseEntity.ok(ApiResponse.success(reportService.getWeeklySummary(userId, date)))
    }
}

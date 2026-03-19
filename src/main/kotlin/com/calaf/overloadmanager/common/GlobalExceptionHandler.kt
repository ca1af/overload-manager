package com.calaf.overloadmanager.common

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AppException::class)
    fun handleAppException(ex: AppException): ResponseEntity<ApiResponse<Nothing>> {
        val error = ErrorResponse(
            code = ex.errorCode.name,
            message = ex.message,
        )
        return ResponseEntity.status(ex.errorCode.status).body(ApiResponse.error(error))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val fieldErrors = ex.bindingResult.fieldErrors.map {
            FieldError(field = it.field, message = it.defaultMessage ?: "Invalid value")
        }
        val error = ErrorResponse(
            code = ErrorCode.INVALID_INPUT.name,
            message = "Validation failed",
            details = fieldErrors,
        )
        return ResponseEntity.badRequest().body(ApiResponse.error(error))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        val error = ErrorResponse(
            code = ErrorCode.INTERNAL_ERROR.name,
            message = "An unexpected error occurred",
        )
        return ResponseEntity.internalServerError().body(ApiResponse.error(error))
    }
}

package com.calaf.overloadmanager.common

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(AppException::class)
    fun handleAppException(ex: AppException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("AppException [{}]: {}", ex.errorCode, ex.message)
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
        log.warn("Validation failed: {}", fieldErrors)
        val error = ErrorResponse(
            code = ErrorCode.INVALID_INPUT.name,
            message = "Validation failed",
            details = fieldErrors,
        )
        return ResponseEntity.badRequest().body(ApiResponse.error(error))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("Malformed request body: {}", ex.message)
        val error = ErrorResponse(
            code = ErrorCode.INVALID_INPUT.name,
            message = "Malformed request body",
        )
        return ResponseEntity.badRequest().body(ApiResponse.error(error))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("Unhandled exception", ex)
        val error = ErrorResponse(
            code = ErrorCode.INTERNAL_ERROR.name,
            message = "An unexpected error occurred",
        )
        return ResponseEntity.internalServerError().body(ApiResponse.error(error))
    }
}

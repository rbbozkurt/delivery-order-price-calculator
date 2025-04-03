package com.wolt.dopc.exception

import com.wolt.dopc.dto.*
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.MissingRequestValueException
import java.time.LocalDateTime

/**
 * Global exception handler for handling various exceptions across the application.
 */
@ControllerAdvice
@RestController
class GlobalExceptionHandler {

    /**
     * Handles missing query parameters.
     *
     * @param ex the exception thrown when a required query parameter is missing.
     * @return a ResponseEntity containing the error response.
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingParams(ex: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Missing required query parameter: '${ex.parameterName}'",
            details = ErrorDetails.MissingQueryParameter(parameter = ex.parameterName)
        )
    }

    @ExceptionHandler(MissingRequestValueException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingRequestValueException(ex: MissingRequestValueException): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Missing required value: '${ex.message}'",
            details = ErrorDetails.MissingQueryParameter(parameter = ex.reason ?: "Unknown parameter")
        )
    }


    /**
     * Handles validation errors.
     *
     * @param ex the exception thrown when validation fails.
     * @return a ResponseEntity containing the error response.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.map { fieldError ->
            ValidationFieldError(
                field = fieldError.field,
                message = fieldError.defaultMessage ?: "Validation error",
                invalidValue = fieldError.rejectedValue
            )
        }

        return buildErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Validation failed",
            details = ErrorDetails.Validation(errors = errors)
        )
    }

    /**
     * Handles constraint violations.
     *
     * @param ex the exception thrown when a constraint is violated.
     * @return a ResponseEntity containing the error response.
     */
    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        val errors = ex.constraintViolations.map { violation ->
            ViolationFieldError(
                field = violation.propertyPath.toString(),
                message = violation.message,
                invalidValue = violation.invalidValue
            )
        }

        return buildErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Constraint violation",
            details = ErrorDetails.ConstraintViolation(errors = errors)
        )
    }

    /**
     * Handles IllegalArgumentException.
     *
     * @param ex the exception thrown when an illegal argument is provided.
     * @return a ResponseEntity containing the error response.
     */
    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = ex.message ?: "Invalid argument provided",
            details = ErrorDetails.Generic(message = ex.message ?: "Unknown error")
        )
    }

    /**
     * Handles ExternalAPIException and provides user-friendly messages.
     *
     * @param ex The exception thrown when there is an external API error.
     * @return A ResponseEntity containing the error response.
     */
    @ExceptionHandler(ExternalApiException::class)
    fun handleExternalAPIException(ex: ExternalApiException): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(
            status = HttpStatus.BAD_GATEWAY,
            message = "An error occurred while communicating with an external service.",
            details = ErrorDetails.ExternalApi(message = ex.message ?: "Unexpected error.")
        )
    }


    /**
     * Handles IllegalStateException for unexpected application state errors.
     *
     * @param ex The exception thrown when there is an illegal state in the application.
     * @return A ResponseEntity containing the error response.
     */
    @ExceptionHandler(IllegalStateException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = "An unexpected internal error occurred.",
            details = ErrorDetails.Generic(message = ex.message ?: "Internal error.")
        )
    }

    /**
     * Handles unexpected exceptions.
     *
     * @param ex the exception thrown when an unexpected error occurs.
     * @return a ResponseEntity containing the error response.
     */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = "An unexpected error occurred",
            details = ErrorDetails.Generic(message = "Please try again later.")
        )
    }

    /**
     * Utility method to build an error response.
     *
     * @param status the HTTP status of the response.
     * @param message the error message.
     * @param details additional details about the error.
     * @return a ResponseEntity containing the error response.
     */
    private fun buildErrorResponse(
        status: HttpStatus,
        message: String,
        details: ErrorDetails? = null
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now().toString(),
            error = message,
            details = details
        )
        return ResponseEntity(errorResponse, status)
    }
}
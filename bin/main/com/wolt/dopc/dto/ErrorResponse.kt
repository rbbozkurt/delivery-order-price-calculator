package com.wolt.dopc.dto

import java.time.LocalDateTime

/**
 * Data class representing the error response.
 *
 * @property timestamp The timestamp when the error occurred.
 * @property error The error message.
 * @property details Additional details about the error.
 */
data class ErrorResponse(
    val timestamp: String = LocalDateTime.now().toString(),
    val error: String,
    val details: ErrorDetails? = null
)

/**
 * Sealed class representing the base class for all error details.
 */
sealed class ErrorDetails {
    /**
     * Data class representing missing query parameter error details.
     *
     * @property parameter The name of the missing parameter.
     */
    data class MissingQueryParameter(val parameter: String) : ErrorDetails()

    /**
     * Data class representing validation errors.
     *
     * @property errors The list of validation field errors.
     */
    data class Validation(val errors: List<ValidationFieldError>) : ErrorDetails()

    /**
     * Data class representing constraint violation errors.
     *
     * @property errors The list of violation field errors.
     */
    data class ConstraintViolation(val errors: List<ViolationFieldError>) : ErrorDetails()

    /**
     * Data class representing generic error details.
     *
     * @property message The error message.
     */
    data class Generic(val message: String) : ErrorDetails()

    data class ExternalApi(val message: String) : ErrorDetails()
}

/**
 * Data class representing field validation error details.
 *
 * @property field The name of the field.
 * @property message The error message.
 * @property invalidValue The invalid value provided.
 */
data class ValidationFieldError(
    val field: String,
    val message: String,
    val invalidValue: Any? = null
)

/**
 * Data class representing constraint violation error details.
 *
 * @property field The name of the field.
 * @property message The error message.
 * @property invalidValue The invalid value provided.
 */
data class ViolationFieldError(
    val field: String,
    val message: String,
    val invalidValue: Any?
)
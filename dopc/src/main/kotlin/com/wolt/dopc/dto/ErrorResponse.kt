package com.wolt.dopc.exception

import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: String = LocalDateTime.now().toString(),
    val error: String,
    val details: ErrorDetails? = null
)

sealed class ErrorDetails {
    data class MissingQueryParameter(val parameter: String) : ErrorDetails()
    data class InvalidParameterType(val parameter: String, val expected_type: String) : ErrorDetails()
    data class ValidationErrors(val errors: List<String>) : ErrorDetails()
    data class GenericError(val message: String) : ErrorDetails()
}

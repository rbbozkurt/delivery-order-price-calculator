package com.wolt.dopc.exception

import com.wolt.dopc.dto.ErrorDetails
import io.mockk.every
import io.mockk.mockk
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.core.MethodParameter
import kotlin.test.assertEquals

class GlobalExceptionHandlerTest {

    private lateinit var globalExceptionHandler: GlobalExceptionHandler

    @BeforeEach
    fun setUp() {
        globalExceptionHandler = GlobalExceptionHandler()
    }

    @Nested
    inner class MissingParamsTests {

        @Test
        fun `should handle MissingServletRequestParameterException`() {
            val ex = MissingServletRequestParameterException("param", "String")
            val response = globalExceptionHandler.handleMissingParams(ex)

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertEquals("Missing required query parameter: 'param'", response.body?.error)
        }
    }

    @Nested
    inner class ValidationErrorsTests {

        @Test
        fun `should handle MethodArgumentNotValidException with single field error`() {
            val fieldError = FieldError("objectName", "field", "invalid value", false, null, null, "Field is invalid")
            val bindException = BindException(Object(), "objectName")
            bindException.addError(fieldError)

            val methodParameter = MethodParameter(GlobalExceptionHandler::class.java.getDeclaredMethod("handleValidationErrors", MethodArgumentNotValidException::class.java), -1)
            val ex = MethodArgumentNotValidException(methodParameter, bindException.bindingResult)
            val response = globalExceptionHandler.handleValidationErrors(ex)

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertEquals("Validation failed", response.body?.error)
            val details = response.body?.details as ErrorDetails.Validation
            assertEquals(1, details.errors.size)
            assertEquals("field", details.errors[0].field)
        }

        @Test
        fun `should handle MethodArgumentNotValidException with multiple field errors`() {
            val bindException = BindException(Object(), "objectName")
            bindException.addError(FieldError("objectName", "field1", "invalid value", false, null, null, "Field 1 is invalid"))
            bindException.addError(FieldError("objectName", "field2", "null value", false, null, null, "Field 2 cannot be null"))

            val methodParameter = MethodParameter(GlobalExceptionHandler::class.java.getDeclaredMethod("handleValidationErrors", MethodArgumentNotValidException::class.java), -1)
            val ex = MethodArgumentNotValidException(methodParameter, bindException.bindingResult)
            val response = globalExceptionHandler.handleValidationErrors(ex)

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertEquals("Validation failed", response.body?.error)
            val details = response.body?.details as ErrorDetails.Validation
            assertEquals(2, details.errors.size)
        }

       @Test
        fun `should handle ConstraintViolationException`() {
            val violation = mockk<ConstraintViolation<*>>()
            every { violation.propertyPath.toString() } returns "field"
            every { violation.message } returns "must not be null"
            every { violation.invalidValue } returns "invalid value"

            val ex = ConstraintViolationException("Constraint violated", setOf(violation))
            val response = globalExceptionHandler.handleConstraintViolationException(ex)

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertEquals("Constraint violation", response.body?.error)
            val details = response.body?.details as ErrorDetails.ConstraintViolation
            assertEquals(1, details.errors.size)
            assertEquals("field", details.errors[0].field)
        }
    }

    @Nested
    inner class IllegalStateExceptionTests {

        @Test
        fun `should handle IllegalArgumentException`() {
            val ex = IllegalArgumentException("Invalid argument provided")
            val response = globalExceptionHandler.handleIllegalArgumentException(ex)

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertEquals("Invalid argument provided", response.body?.error)
        }

        @Test
        fun `should handle IllegalStateException`() {
            val ex = IllegalStateException("Service unavailable")
            val response = globalExceptionHandler.handleIllegalStateException(ex)

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
            assertEquals("An unexpected internal error occurred.", response.body?.error)
        }
    }

    @Nested
    inner class GenericExceptionTests {

        @Test
        fun `should handle unexpected Exception`() {
            val ex = Exception("Unexpected error occurred")
            val response = globalExceptionHandler.handleGenericException(ex)

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
            assertEquals("An unexpected error occurred", response.body?.error)
        }
    }

    @Nested
    inner class EdgeCaseTests {

        @Test
        fun `should handle MethodArgumentNotValidException with no errors`() {
            val bindException = BindException(Object(), "objectName")
            val methodParameter = MethodParameter(GlobalExceptionHandler::class.java.getDeclaredMethod("handleValidationErrors", MethodArgumentNotValidException::class.java), -1)
            val ex = MethodArgumentNotValidException(methodParameter, bindException.bindingResult)

            val response = globalExceptionHandler.handleValidationErrors(ex)

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertEquals("Validation failed", response.body?.error)
            val details = response.body?.details as ErrorDetails.Validation
            assertEquals(0, details.errors.size)
        }

        @Test
        fun `should handle ConstraintViolationException with empty violations`() {
            val ex = ConstraintViolationException("Constraint violated", emptySet())
            val response = globalExceptionHandler.handleConstraintViolationException(ex)

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertEquals("Constraint violation", response.body?.error)
            val details = response.body?.details as ErrorDetails.ConstraintViolation
            assertEquals(0, details.errors.size)
        }
    }
}

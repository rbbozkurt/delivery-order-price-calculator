package com.wolt.dopc.exception

/**
 * Exception thrown when an error occurs while calling an external API.
 *
 * @param message The detail message (which is saved for later retrieval by the [Throwable.getMessage] method).
 * @param cause The cause (which is saved for later retrieval by the [Throwable.getCause] method). (A `null` value is permitted, and indicates that the cause is nonexistent or unknown.)
 */
class ExternalApiException : Exception {
    constructor(message: String = "An error occurred while calling the external API") : super(message)
    constructor(message: String = "An error occurred while calling the external API", cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super("An error occurred while calling the external API", cause)
}
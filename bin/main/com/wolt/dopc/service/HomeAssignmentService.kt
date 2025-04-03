package com.wolt.dopc.service

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.DeserializationFeature
import com.wolt.dopc.config.ApiConfig
import com.wolt.dopc.exception.ExternalApiException
import com.wolt.dopc.model.VenueDynamicInfo
import com.wolt.dopc.model.VenueDynamicInfoResponse
import com.wolt.dopc.model.VenueStaticInfo
import com.wolt.dopc.model.VenueStaticInfoResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.slf4j.LoggerFactory

/**
 * Service class for handling home assignment-related operations.
 */
@Service
class HomeAssignmentService(
    private val webClient: WebClient,
    private val objectMapper: ObjectMapper,
    private val apiConfig: ApiConfig // Injecting ApiConfig
) {

    private val logger = LoggerFactory.getLogger(HomeAssignmentService::class.java)

    init {
        // Configure ObjectMapper to ignore unknown properties
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    /**
     * Fetches static information about a venue.
     *
     * @param venueSlug The slug identifier for the venue.
     * @return The static information of the venue.
     * @throws ExternalApiException If there is an error fetching or validating the data.
     */
    fun getStaticInfo(venueSlug: String): VenueStaticInfo {
        val url = apiConfig.getVenueStaticUrl(venueSlug) // Using the configuration class
        val venueStaticInfoResponse: VenueStaticInfoResponse = fetchApiResponse(url, VenueStaticInfoResponse::class.java)
        return venueStaticInfoResponse.toVenueStaticInfo()
    }

    /**
     * Fetches dynamic information about a venue.
     *
     * @param venueSlug The slug identifier for the venue.
     * @return The dynamic information of the venue.
     * @throws ExternalApiException If there is an error fetching or validating the data.
     */
    fun getDynamicInfo(venueSlug: String): VenueDynamicInfo {
        val url = apiConfig.getVenueDynamicUrl(venueSlug) // Using the configuration class
        val venueDynamicInfoResponse: VenueDynamicInfoResponse = fetchApiResponse(url, VenueDynamicInfoResponse::class.java)
        return venueDynamicInfoResponse.toVenueDynamicInfo()
    }

    /**
     * Fetches the API response from the given URL and maps it to the specified response type.
     *
     * @param url The URL to fetch the API response from.
     * @param responseType The class type to map the response to.
     * @param T The type of the response.
     * @return The mapped response object.
     * @throws ExternalApiException If there is an error fetching, parsing, or validating the data.
     */
    private fun <T> fetchApiResponse(url: String, responseType: Class<T>): T {
        return try {
            val response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: throw ExternalApiException("Empty response from $url")

            val parsedResponse = objectMapper.readValue(response, responseType)
            validateParsedResponse(parsedResponse, url)
            parsedResponse
        } catch (ex: Exception) {
            when (ex) {
                is WebClientResponseException -> handleWebClientException(ex, url)
                is JsonMappingException -> throw IllegalStateException("Error parsing response from $url", ex)
                else -> throw ExternalApiException("Unexpected error when fetching data from $url", ex)
            }
        }
    }

    /**
     * Validates the parsed response for null or invalid data.
     *
     * @param parsedResponse The response object parsed from JSON.
     * @param url The URL of the API endpoint.
     * @param T The type of the response.
     * @throws ExternalApiException If the parsed data is invalid.
     */
    private fun <T> validateParsedResponse(parsedResponse: T, url: String) {
        when (parsedResponse) {
            is VenueStaticInfoResponse -> {
                if (parsedResponse.venueRaw.location.coordinates.isEmpty()) {
                    throw ExternalApiException("Invalid venue coordinates in the static info response from $url")
                }
            }
            is VenueDynamicInfoResponse -> {
                if (parsedResponse.venueRaw.deliverySpecs.deliveryPricing.distanceRanges.isEmpty()) {
                    throw ExternalApiException("Distance ranges are missing in the dynamic info response from $url")
                }
                if (parsedResponse.venueRaw.deliverySpecs.deliveryPricing.distanceRanges.any { it.min > it.max && it.max != 0 }) {
                    throw ExternalApiException("Invalid distance range values in the dynamic info response from $url")
                }
            }
            else -> {
                logger.warn("Unexpected response type for validation from $url")
            }
        }
    }

    /**
     * Handles WebClientResponseException and throws appropriate exceptions.
     *
     * @param ex The WebClientResponseException instance.
     * @param url The URL of the failed request.
     * @throws ExternalApiException The exception with a user-friendly message.
     */
    private fun handleWebClientException(ex: WebClientResponseException, url: String): Nothing {
        when (ex.statusCode) {
            HttpStatus.NOT_FOUND -> throw ExternalApiException("Resource not found at $url")
            HttpStatus.BAD_REQUEST -> throw ExternalApiException("Bad request sent to $url: ${ex.message}")
            HttpStatus.TOO_MANY_REQUESTS -> throw ExternalApiException("Rate limit exceeded when accessing $url")
            HttpStatus.INTERNAL_SERVER_ERROR -> throw ExternalApiException("External server error at $url: ${ex.message}")
            else -> throw ExternalApiException("HTTP error from external API at $url: ${ex.statusCode} - ${ex.message}")
        }
    }
}

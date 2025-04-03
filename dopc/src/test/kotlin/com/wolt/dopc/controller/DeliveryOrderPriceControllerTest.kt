package com.wolt.dopc.controller

import com.wolt.dopc.model.DeliveryCalculationResult
import com.wolt.dopc.service.DeliveryOrderPriceService
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@ActiveProfiles("test")
@WebMvcTest(DeliveryOrderPriceController::class)
class DeliveryOrderPriceControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var deliveryOrderPriceService: DeliveryOrderPriceService

    private fun buildUrl(
        venueSlug: String? = null,
        cartValue: Int? = null,
        userLat: Double? = null,
        userLon: Double? = null
    ): String {
        return "/api/v1/delivery-order-price?" +
                listOfNotNull(
                    venueSlug?.let { "venue_slug=$it" },
                    cartValue?.let { "cart_value=$it" },
                    userLat?.let { "user_lat=$it" },
                    userLon?.let { "user_lon=$it" }
                ).joinToString("&")
    }

    private fun mockServiceResponse(
        venueSlug: String,
        cartValue: Int,
        userLat: Double,
        userLon: Double,
        response: DeliveryCalculationResult
    ) {
        `when`(
            deliveryOrderPriceService.calculateDeliveryData(venueSlug, cartValue, userLat, userLon)
        ).thenReturn(response)
    }

    @Nested
    inner class SuccessScenarios {

        @Test
        fun `should return delivery order price successfully`() {
            val venueSlug = "test-venue"
            val cartValue = 1000
            val userLat = 60.17094
            val userLon = 24.93087

            val expectedResponse = DeliveryCalculationResult(
                totalPrice = 1590,
                smallOrderSurcharge = 0,
                deliveryFee = 590,
                deliveryDistance = 12300
            )

            mockServiceResponse(venueSlug, cartValue, userLat, userLon, expectedResponse)

            mockMvc.get(buildUrl(venueSlug, cartValue, userLat, userLon))
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.total_price") { value(1590) }
                    jsonPath("$.small_order_surcharge") { value(0) }
                    jsonPath("$.cart_value") { value(cartValue) }
                    jsonPath("$.delivery.fee") { value(590) }
                    jsonPath("$.delivery.distance") { value(12300) }
                }
        }
    }

    @Nested
    inner class ValidationErrors {

        @Test
        fun `should return bad request when venue_slug is missing`() {
            mockMvc.get(buildUrl(cartValue = 1000, userLat = 60.17094, userLon = 24.93087))
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.error") { value("Missing required query parameter: 'venue_slug'") }
                }
        }

        @Test
        fun `should return bad request when cart_value is negative`() {
            mockMvc.get(buildUrl(venueSlug = "test-venue", cartValue = -100, userLat = 60.17094, userLon = 24.93087))
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.error") { value("Constraint violation") }
                }
        }

        @Test
        fun `should return bad request when user_lat exceeds bounds`() {
            mockMvc.get(buildUrl(venueSlug = "test-venue", cartValue = 1000, userLat = 200.0, userLon = 24.93087))
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.error") { value("Constraint violation") }
                }
        }

        @Test
        fun `should return bad request when user_lon exceeds bounds`() {
            mockMvc.get(buildUrl(venueSlug = "test-venue", cartValue = 1000, userLat = 60.17094, userLon = 200.0))
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.error") { value("Constraint violation") }
                }
        }
    }

    @Nested
    inner class UnexpectedErrors {

        @Test
        fun `should return bad request when delivery is not possible`() {
            val venueSlug = "test-venue"
            val cartValue = 1000
            val userLat = 60.17094
            val userLon = 24.93087

            `when`(
                deliveryOrderPriceService.calculateDeliveryData(venueSlug, cartValue, userLat, userLon)
            ).thenThrow(IllegalArgumentException("Delivery is not possible"))

            mockMvc.get(buildUrl(venueSlug, cartValue, userLat, userLon))
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.error") { value("Delivery is not possible") }
                }
        }

        @Test
        fun `should return internal server error for unexpected exceptions`() {
            val venueSlug = "test-venue"
            val cartValue = 1000
            val userLat = 60.17094
            val userLon = 24.93087

            `when`(
                deliveryOrderPriceService.calculateDeliveryData(venueSlug, cartValue, userLat, userLon)
            ).thenThrow(RuntimeException("Unexpected error"))

            mockMvc.get(buildUrl(venueSlug, cartValue, userLat, userLon))
                .andExpect {
                    status { isInternalServerError() }
                    jsonPath("$.error") { value("An unexpected error occurred") }
                }
        }
    }

    @Nested
    inner class EdgeCaseTests {

        @Test
        fun `should return bad request when cart_value is zero`() {
            mockMvc.get(buildUrl(venueSlug = "test-venue", cartValue = 0, userLat = 60.17094, userLon = 24.93087))
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.error") { value("Constraint violation") }
                }
        }

        @Test
        fun `should return bad request when user_lat is missing`() {
            mockMvc.get(buildUrl(venueSlug = "test-venue", cartValue = 1000, userLon = 24.93087))
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.error") { value("Missing required query parameter: 'user_lat'") }
                }
        }

        @Test
        fun `should return bad request when user_lon is missing`() {
            mockMvc.get(buildUrl(venueSlug = "test-venue", cartValue = 1000, userLat = 60.17094))
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.error") { value("Missing required query parameter: 'user_lon'") }
                }
        }
    }
}
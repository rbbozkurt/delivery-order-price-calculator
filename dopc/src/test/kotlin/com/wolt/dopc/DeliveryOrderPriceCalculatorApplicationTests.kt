package com.wolt.dopc

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeliveryOrderPriceCalculatorApplicationTests {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `context loads`() {
        // Ensures the application context loads successfully
    }

    @Nested
    inner class SuccessScenarios {

        @Test
        fun `should return delivery order price successfully`() {
            val response = restTemplate.getForEntity(
                "/api/v1/delivery-order-price?venue_slug=test-venue&cart_value=1000&user_lat=60.17094&user_lon=24.93087",
                String::class.java
            )

            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body)
        }
    }

    @Nested
    inner class ErrorScenarios {

        @Test
        fun `should return bad request when missing venue_slug`() {
            val response = restTemplate.getForEntity(
                "/api/v1/delivery-order-price?cart_value=1000&user_lat=60.17094&user_lon=24.93087",
                String::class.java
            )

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertNotNull(response.body)
        }

        @Test
        fun `should return bad request when cart_value is negative`() {
            val response = restTemplate.getForEntity(
                "/api/v1/delivery-order-price?venue_slug=test-venue&cart_value=-100&user_lat=60.17094&user_lon=24.93087",
                String::class.java
            )

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertNotNull(response.body)
        }

        @Test
        fun `should return bad request when cart_value is zero`() {
            val response = restTemplate.getForEntity(
                "/api/v1/delivery-order-price?venue_slug=test-venue&cart_value=0&user_lat=60.17094&user_lon=24.93087",
                String::class.java
            )

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertNotNull(response.body)
        }

        @Test
        fun `should return bad request when cart_value exceeded`() {
            val response = restTemplate.getForEntity(
                "/api/v1/delivery-order-price?venue_slug=test-venue&cart_value=1000001&user_lat=60.17094&user_lon=24.93087",
                String::class.java
            )

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertNotNull(response.body)
        }

        @Test
        fun `should return bad request when user_lat exceeds bounds`() {
            val response = restTemplate.getForEntity(
                "/api/v1/delivery-order-price?venue_slug=test-venue&cart_value=1000&user_lat=200.0&user_lon=24.93087",
                String::class.java
            )

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertNotNull(response.body)
        }

        @Test
        fun `should return bad request when user_lon exceeds bounds`() {
            val response = restTemplate.getForEntity(
                "/api/v1/delivery-order-price?venue_slug=test-venue&cart_value=1000&user_lat=60.17094&user_lon=200.0",
                String::class.java
            )

            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
            assertNotNull(response.body)
        }

        @Test
        fun `should return bad gateway when external APIs unreachable`() {
            val response = restTemplate.getForEntity(
                "/api/v1/delivery-order-price?venue_slug=unknown-venue&cart_value=1000&user_lat=60.17094&user_lon=24.93087",
                String::class.java
            )

            assertEquals(HttpStatus.BAD_GATEWAY, response.statusCode)
            assertNotNull(response.body)
        }
    }

    @Nested
    inner class EdgeCases {

        @Test
        fun `should handle cart_value at maximum boundary`() {
            val response = restTemplate.getForEntity(
                "/api/v1/delivery-order-price?venue_slug=test-venue&cart_value=1000000&user_lat=60.17094&user_lon=24.93087",
                String::class.java
            )

            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body)
        }

        @Test
        fun `should handle cart_value at minimum boundary`() {
            val response = restTemplate.getForEntity(
                "/api/v1/delivery-order-price?venue_slug=test-venue&cart_value=1&user_lat=60.17094&user_lon=24.93087",
                String::class.java
            )

            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body)
        }


        @Test
        fun `should handle zero values for coordinates`() {
            val response = restTemplate.getForEntity(
                "/api/v1/delivery-order-price?venue_slug=test-venue&cart_value=1000&user_lat=0.0&user_lon=0.0",
                String::class.java
            )

            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body)
        }
    }
}
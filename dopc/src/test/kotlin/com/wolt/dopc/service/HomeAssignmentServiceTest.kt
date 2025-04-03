import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.wolt.dopc.config.ApiConfig
import com.wolt.dopc.exception.ExternalApiException
import com.wolt.dopc.model.VenueDynamicInfoResponse
import com.wolt.dopc.model.VenueStaticInfoResponse
import com.wolt.dopc.service.HomeAssignmentService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import io.mockk.every
import io.mockk.mockk

class HomeAssignmentServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var homeAssignmentService: HomeAssignmentService
    private lateinit var objectMapper: ObjectMapper
    private lateinit var apiConfig: ApiConfig

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        objectMapper = ObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
        }

        apiConfig = mockk()
        every { apiConfig.getVenueStaticUrl(any()) } answers {
            val venueSlug = it.invocation.args[0] as String
            "${mockWebServer.url("/home-assignment-api/v1/venues/$venueSlug/static")}".toString()
        }
        every { apiConfig.getVenueDynamicUrl(any()) } answers {
            val venueSlug = it.invocation.args[0] as String
            "${mockWebServer.url("/home-assignment-api/v1/venues/$venueSlug/dynamic")}".toString()
        }

        val webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build()
        homeAssignmentService = HomeAssignmentService(webClient, objectMapper, apiConfig)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Nested
    inner class StaticInfoTests {

        @Test
        fun `should return static info successfully`() {
            val venueSlug = "test-venue"
            val expectedResponse = VenueStaticInfoResponse(
                venueRaw = VenueStaticInfoResponse.VenueRaw(
                    location = VenueStaticInfoResponse.VenueRaw.Location(
                        coordinates = listOf(60.16952, 24.93545)
                    )
                )
            )

            val jsonResponse = objectMapper.writeValueAsString(expectedResponse)
            mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

            val result = homeAssignmentService.getStaticInfo(venueSlug)
            assertEquals(expectedResponse.toVenueStaticInfo(), result)
        }

        @Test
        fun `should throw exception when static info API returns 404`() {
            val venueSlug = "invalid-venue"
            mockWebServer.enqueue(MockResponse().setResponseCode(404))

            val exception = assertFailsWith<ExternalApiException> {
                homeAssignmentService.getStaticInfo(venueSlug)
            }
            assertEquals("Resource not found at ${mockWebServer.url("/home-assignment-api/v1/venues/invalid-venue/static")}", exception.message)
        }

        @Test
        fun `should throw exception when static info response is invalid`() {
            val venueSlug = "test-venue"
            val jsonResponse = "{}" // Invalid response
            mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

            val exception = assertFailsWith<IllegalStateException> {
                homeAssignmentService.getStaticInfo(venueSlug)
            }
            assertEquals("Error parsing response from ${mockWebServer.url("/home-assignment-api/v1/venues/test-venue/static")}", exception.message)
        }
    }

    @Nested
    inner class DynamicInfoTests {

        @Test
        fun `should return dynamic info successfully`() {
            val venueSlug = "test-venue"
            val expectedResponse = VenueDynamicInfoResponse(
                VenueDynamicInfoResponse.VenueRaw(
                    VenueDynamicInfoResponse.VenueRaw.DeliverySpecs(
                        orderMinimumNoSurcharge = 1000,
                        deliveryPricing = VenueDynamicInfoResponse.VenueRaw.DeliverySpecs.DeliveryPricing(
                            basePrice = 490,
                            distanceRanges = listOf(
                                VenueDynamicInfoResponse.VenueRaw.DeliverySpecs.DeliveryPricing.DistanceRange(
                                    min = 0,
                                    max = 1000,
                                    a = 100,
                                    b = 1
                                )
                            )
                        )
                    )
                )
            )

            val jsonResponse = objectMapper.writeValueAsString(expectedResponse)
            mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

            val result = homeAssignmentService.getDynamicInfo(venueSlug)
            assertEquals(expectedResponse.toVenueDynamicInfo(), result)
        }

        @Test
        fun `should throw exception when dynamic info API returns 404`() {
            val venueSlug = "invalid-venue"
            mockWebServer.enqueue(MockResponse().setResponseCode(404))

            val exception = assertFailsWith<ExternalApiException> {
                homeAssignmentService.getDynamicInfo(venueSlug)
            }
            assertEquals("Resource not found at ${mockWebServer.url("/home-assignment-api/v1/venues/invalid-venue/dynamic")}", exception.message)
        }

        @Test
        fun `should throw exception when dynamic info response is invalid`() {
            val venueSlug = "test-venue"
            val jsonResponse = "{}" // Invalid response
            mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

            val exception = assertFailsWith<IllegalStateException> {
                homeAssignmentService.getDynamicInfo(venueSlug)
            }
            assertEquals("Error parsing response from ${mockWebServer.url("/home-assignment-api/v1/venues/test-venue/dynamic")}", exception.message)
        }
    }
}

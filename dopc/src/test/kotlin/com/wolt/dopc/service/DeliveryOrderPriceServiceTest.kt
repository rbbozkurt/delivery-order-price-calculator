import com.wolt.dopc.model.DeliveryCalculationResult
import com.wolt.dopc.model.VenueDynamicInfo
import com.wolt.dopc.model.VenueStaticInfo
import com.wolt.dopc.service.DeliveryOrderPriceService
import com.wolt.dopc.service.HomeAssignmentService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeliveryOrderPriceServiceTest {

    private lateinit var homeAssignmentService: HomeAssignmentService
    private lateinit var deliveryOrderPriceService: DeliveryOrderPriceService

    @BeforeEach
    fun setUp() {
        homeAssignmentService = mockk()
        deliveryOrderPriceService = DeliveryOrderPriceService(homeAssignmentService)
    }

    @Nested
    inner class SuccessfulCalculations {

        @Test
        fun `should calculate delivery price for home-assignment-venue-helsinki successfully`() {
            val venueSlug = "home-assignment-venue-helsinki"
            val staticInfo = VenueStaticInfo(coordinates = listOf(24.92813512, 60.17012143))
            val dynamicInfo = VenueDynamicInfo(
                orderMinimumNoSurcharge = 1000,
                basePrice = 190,
                distanceRanges = listOf(
                    VenueDynamicInfo.DistanceRange(min = 0, max = 500, a = 0, b = 0),
                    VenueDynamicInfo.DistanceRange(min = 500, max = 1000, a = 100, b = 0),
                    VenueDynamicInfo.DistanceRange(min = 1000, max = 1500, a = 200, b = 0),
                    VenueDynamicInfo.DistanceRange(min = 1500, max = 2000, a = 200, b = 1),
                    VenueDynamicInfo.DistanceRange(min = 2000, max = 0, a = 0, b = 0)
                )
            )

            every { homeAssignmentService.getStaticInfo(venueSlug) } returns staticInfo
            every { homeAssignmentService.getDynamicInfo(venueSlug) } returns dynamicInfo

            val result = deliveryOrderPriceService.calculateDeliveryData(
                venueSlug = venueSlug,
                cartValue = 1000,
                userLat = 60.17094,
                userLon = 24.93087
            )

            assertEquals(
                DeliveryCalculationResult(
                    totalPrice = 1190,
                    smallOrderSurcharge = 0,
                    deliveryFee = 190,
                    deliveryDistance = 177
                ),
                result,
                "Delivery calculation should match the expected result"
            )
        }
    }

    @Nested
    inner class ValidationErrors {

        @Test
        fun `should throw exception for unsupported delivery distance`() {
            val staticInfo = VenueStaticInfo(coordinates = listOf(24.93545, 60.16952))
            val dynamicInfo = VenueDynamicInfo(
                orderMinimumNoSurcharge = 1000,
                basePrice = 490,
                distanceRanges = listOf(
                    VenueDynamicInfo.DistanceRange(min = 0, max = 1000, a = 100, b = 1)
                )
            )

            every { homeAssignmentService.getStaticInfo("test-venue") } returns staticInfo
            every { homeAssignmentService.getDynamicInfo("test-venue") } returns dynamicInfo

            assertFailsWith<IllegalArgumentException>("Delivery not possible for distance: 4755080 meters") {
                deliveryOrderPriceService.calculateDeliveryData(
                    venueSlug = "test-venue",
                    cartValue = 800,
                    userLat = 0.0, // Intentionally far coordinates
                    userLon = 0.0
                )
            }
        }

        @Test
        fun `should throw exception for invalid cart value`() {
            assertFailsWith<IllegalArgumentException>("Cart value must be greater than 0") {
                deliveryOrderPriceService.calculateDeliveryData(
                    venueSlug = "test-venue",
                    cartValue = -500,
                    userLat = 60.17094,
                    userLon = 24.93087
                )
            }
        }

        @Test
        fun `should throw exception for invalid venue coordinates`() {
            val staticInfo = VenueStaticInfo(coordinates = emptyList())
            val dynamicInfo = VenueDynamicInfo(
                orderMinimumNoSurcharge = 1000,
                basePrice = 490,
                distanceRanges = listOf(
                    VenueDynamicInfo.DistanceRange(min = 0, max = 1000, a = 100, b = 1)
                )
            )

            every { homeAssignmentService.getStaticInfo("test-venue") } returns staticInfo
            every { homeAssignmentService.getDynamicInfo("test-venue") } returns dynamicInfo

            assertFailsWith<IllegalStateException>("Invalid venue coordinates: []") {
                deliveryOrderPriceService.calculateDeliveryData(
                    venueSlug = "test-venue",
                    cartValue = 800,
                    userLat = 60.17094,
                    userLon = 24.93087
                )
            }
        }
    }
}

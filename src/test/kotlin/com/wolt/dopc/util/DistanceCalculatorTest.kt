package com.wolt.dopc.util

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class DistanceCalculatorTest {

    private val tolerance = 10.0 // Allowable error in meters

    @Nested
    inner class ValidCoordinates{
        @Test
        fun `calculate distance between two identical points`() {
            val lat = 60.17094
            val lon = 24.93087

            val distance = DistanceCalculator.calculateDistance(lat, lon, lat, lon)

            assertEquals(0.0, distance.toDouble(), tolerance, "Distance between identical points should be 0")
        }


        @Test
        fun `calculate distance between two points`() {
            val lat1 = 60.17094
            val lon1 = 24.93087
            val lat2 = 60.17012143
            val lon2 = 24.92813512

            val distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2)

            assertEquals(177.0, distance.toDouble(), "Distance should match the expected result (approx. 134 meters)")
        }

        @Test
        fun `calculate distance between two nearby points`() {
            val lat1 = 60.17094
            val lon1 = 24.93087
            val lat2 = 60.17194
            val lon2 = 24.93187

            val distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2)

            assertEquals(134.0, distance.toDouble(), tolerance, "Distance should match the expected result (approx. 134 meters)")
        }

        @Test
        fun `calculate distance between two far apart points`() {
            val lat1 = 40.712776 // New York
            val lon1 = -74.005974
            val lat2 = 34.052235 // Los Angeles
            val lon2 = -118.243683

            val distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2)

            assertEquals(3935746.0, distance.toDouble(), tolerance, "Distance should match the expected result (approx. 3935 km)")
        }

        @Test
        fun `calculate distance between points across the equator`() {
            val lat1 = 1.0
            val lon1 = 101.0
            val lat2 = -1.0
            val lon2 = 101.0

            val distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2)

            assertEquals(222389.0, distance.toDouble(), tolerance, "Distance should match the expected result (approx. 222 km across the equator)")
        }

        @Test
        fun `calculate distance between antipodal points`() {
            val lat1 = 0.0
            val lon1 = 0.0
            val lat2 = -0.0
            val lon2 = 180.0

            val distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2)

            assertEquals(20015087.0, distance.toDouble(), tolerance, "Distance should match the Earth's diameter in meters (approx. 20,015 km)")
        }

        @Test
        fun `calculate distance with maximum latitude and longitude`() {
            val lat1 = 90.0
            val lon1 = 180.0
            val lat2 = -90.0
            val lon2 = -180.0

            val distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2)

            assertEquals(20015087.0, distance.toDouble(), tolerance, "Distance should match the Earth's diameter in meters (approx. 20,015 km)")
        }

        @Test
        fun `calculate distance with very small difference`() {
            val lat1 = 60.17094
            val lon1 = 24.93087
            val lat2 = 60.17095
            val lon2 = 24.93088

            val distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2)

            assertEquals(1.0, distance.toDouble(), tolerance, "Distance should match the expected result (approx. 1 meter)")
        }



    }

    @Nested
    inner class InvalidCoordinates {

        @Test
        fun `throws error for invalid latitude`() {
            val exception = assertThrows<IllegalArgumentException> {
                DistanceCalculator.calculateDistance(91.0, 0.0, 0.0, 0.0)
            }
            assertTrue(
                exception.message!!.contains("Invalid latitude"),
                "Expected exception message to contain 'Invalid latitude', but was: ${exception.message}"
            )
        }

        @Test
        fun `throws error for invalid longitude`() {
            val exception = assertThrows<IllegalArgumentException> {
                DistanceCalculator.calculateDistance(0.0, 181.0, 0.0, 0.0)
            }
            assertTrue(
                exception.message!!.contains("Invalid longitude"),
                "Expected exception message to contain 'Invalid longitude', but was: ${exception.message}"
            )
        }

        @Test
        fun `throws error for invalid coordinates in both inputs`() {
            val exception = assertThrows<IllegalArgumentException> {
                DistanceCalculator.calculateDistance(-91.0, -181.0, 0.0, 0.0)
            }
            assertTrue(
                exception.message!!.contains("Invalid latitude"),
                "Expected exception message to contain 'Invalid latitude', but was: ${exception.message}"
            )
        }
    }
}

package com.wolt.dopc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Main application class for the Delivery Order Price Calculator.
 */
@SpringBootApplication
class DeliveryOrderPriceCalculatorApplication

/**
 * The main entry point of the application.
 *
 * @param args The command line arguments.
 */
fun main(args: Array<String>) {
    runApplication<DeliveryOrderPriceCalculatorApplication>(*args)
}
package com.wolt.dopc.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * Configuration class for setting up WebClient bean.
 */
@Configuration
class WebClientConfig {

    /**
     * Creates and configures a WebClient bean.
     *
     * @return a configured WebClient instance.
     */
    @Bean
    fun webClient(): WebClient {
        return WebClient.builder().build()
    }
}
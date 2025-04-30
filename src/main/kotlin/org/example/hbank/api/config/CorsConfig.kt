package org.example.hbank.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration(proxyBeanMethods = false)
class CorsConfig {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration
            .allowedOrigins = mutableListOf() //mutableListOf("http://localhost:4200/")
        corsConfiguration
            .allowedHeaders = mutableListOf("*")
        corsConfiguration
            .allowedMethods = mutableListOf(
            HttpMethod.DELETE.name(),
            HttpMethod.GET.name(),
            HttpMethod.HEAD.name(),
            HttpMethod.OPTIONS.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.TRACE.name()
        )

        corsConfiguration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration(
            "/**", corsConfiguration
        )
        return source
    }
}
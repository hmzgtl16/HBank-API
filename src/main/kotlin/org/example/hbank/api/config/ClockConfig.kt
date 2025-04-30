package org.example.hbank.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.ZoneId

@Configuration(proxyBeanMethods = false)
class ClockConfig {

    @Bean
    fun provideClock(): Clock = Clock.system(ZoneId.of("Africa/Algiers"))
}

package org.example.hbank.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication(proxyBeanMethods = false)
@ConfigurationPropertiesScan
@EnableAsync
class HBankApiApplication

fun main(args: Array<String>) {
    runApplication<HBankApiApplication>(*args)
}

package org.example.hbank.api.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container

@TestConfiguration
class TestContainersConfig {

    companion object {

        @Container
        @ServiceConnection
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:13.21")
            .withDatabaseName("hbank_test_db")
            .withUsername("hbank_test_root")
            .withPassword("hbank_test_pass")
            .withInitScript("database/schema.sql")
    }

    init {
        postgres.start()
    }

    @DynamicPropertySource
    fun dataSourceProperties(registry: DynamicPropertyRegistry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl)
        registry.add("spring.datasource.username", postgres::getUsername)
        registry.add("spring.datasource.password", postgres::getPassword)
    }
}
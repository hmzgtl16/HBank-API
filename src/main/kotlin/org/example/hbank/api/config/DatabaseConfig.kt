package org.example.hbank.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableJpaRepositories(basePackages = ["org.example.hbank.api"])
@EnableTransactionManagement
class DatabaseConfig
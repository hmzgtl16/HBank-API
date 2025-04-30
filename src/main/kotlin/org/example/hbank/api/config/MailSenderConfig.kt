package org.example.hbank.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration(proxyBeanMethods = false)
class MailSenderConfig(
    @Value("\${spring.mail.host}")
    private val mailServerHost: String,
    @Value("\${spring.mail.port}")
    private val mailServerPort: Int,
    @Value("\${spring.mail.username}")
    private val mailServerUsername: String,
    @Value("\${spring.mail.password}")
    private val mailServerPassword: String,
    @Value("\${spring.mail.protocol}")
    private val mailServerProtocol: String
) {

    @Bean
    fun provideMailSender(): JavaMailSender = JavaMailSenderImpl().apply {

        host = mailServerHost
        port = mailServerPort
        username = mailServerUsername
        password = mailServerPassword
        protocol = mailServerProtocol

       /* javaMailProperties.let {
            it["spring.mail.properties.mail.smtp.auth"] = "true"
            it["spring.mail.properties.mail.smtp.starttls.enable"] = "true"
            it["spring.mail.properties.mail.debug"] = "true"
        }*/
    }
}

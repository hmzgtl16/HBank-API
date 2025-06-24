package org.example.hbank.api.config

import org.example.hbank.api.util.Privileges
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity
class WebSecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf(CsrfConfigurer<HttpSecurity>::disable)
            .cors(CorsConfigurer<HttpSecurity>::disable)
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .oauth2ResourceServer {
                it.jwt(Customizer.withDefaults())
            }

        return http.build()
    }

    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService {
        val user = User.builder()
            .username("test")
            .password(passwordEncoder.encode("test"))
            .roles(Privileges.getAllPrivileges())
            .build()
        return InMemoryUserDetailsManager(user)
    }
}
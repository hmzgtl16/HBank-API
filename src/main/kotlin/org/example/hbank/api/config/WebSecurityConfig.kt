package org.example.hbank.api.config

import org.example.hbank.api.utility.Privileges
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfigurationSource


@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    private val corsConfigurationSource: CorsConfigurationSource
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource) }
            .csrf(CsrfConfigurer<HttpSecurity>::disable)
            .authorizeHttpRequests { it ->
              /*  it
                    .dispatcherTypeMatchers(DispatcherType.ERROR)
                    .permitAll()*/

                it
                    .requestMatchers("/api/v1/user/**")
                    .permitAll()

                it
                    .requestMatchers("/api/v1/customer/**")
                    .hasAuthority(Privileges.PRIVILEGE_CUSTOMER_READ)

                it
                    .requestMatchers(
                        "/api/v1/account",
                        "/api/v1/account/personal",
                        "/api/v1/account/personal/outdated"
                    )
                    .hasAuthority(Privileges.PRIVILEGE_ACCOUNT_READ)

                it
                    .requestMatchers("/api/v1/account/token")
                    .hasAuthority(Privileges.PRIVILEGE_ACCOUNT_WRITE)

                it
                    .requestMatchers(
                        "/api/v1/transaction",
                        "/api/v1/transaction/last"
                    )
                    .hasAuthority(Privileges.PRIVILEGE_TRANSACTION_READ)
                it
                    .requestMatchers(
                        "/api/v1/transaction/transfer/create",
                        "/api/v1/transaction/transfer/verify",
                        "/api/v1/transaction/transfer/confirm",
                        "/api/v1/transaction/transfer/complete",
                        "/api/v1/transaction/transfer/cancel",
                        "/api/v1/transaction/request/create",
                        "/api/v1/transaction/request/verify",
                        "/api/v1/transaction/request/confirm",
                        "/api/v1/transaction/request/complete",
                        "/api/v1/transaction/request/cancel"
                    )
                    .hasAuthority(Privileges.PRIVILEGE_TRANSACTION_WRITE)

                it
                    .anyRequest()
                    .authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .oauth2ResourceServer {
                it.jwt(Customizer.withDefaults())
            }
            .exceptionHandling {
                it.authenticationEntryPoint(BearerTokenAuthenticationEntryPoint())
                it.accessDeniedHandler(BearerTokenAccessDeniedHandler())
            }
            .httpBasic(Customizer.withDefaults())

        return http.build()
    }
}

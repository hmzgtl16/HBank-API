package org.example.hbank.api.controller

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.BaseIntegrationTest
import org.example.hbank.api.config.WebSecurityConfig
import org.example.hbank.api.request.RegisterUserRequest
import org.example.hbank.api.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doNothing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.web.context.WebApplicationContext

@Import(WebSecurityConfig::class)
class UserControllerIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var mockMvcTester: MockMvcTester

    @MockitoBean
    private lateinit var userService: UserService

    @Test
    fun `should register user successfully`() {
        val request = RegisterUserRequest(
            username = "testuser",
            email = "test@example.com",
            password = "Test123!"
        )

        doNothing().`when`(userService).registerUser(request = request)

        val result = mockMvcTester
            .post()
            .uri("/api/v1/users/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(Json.encodeToString(value = request))
            .exchange()

        assertThat(result).hasStatus(HttpStatus.CREATED)
    }
}
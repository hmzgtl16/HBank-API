package org.example.hbank.api.mapper

import org.example.hbank.api.model.User
import org.example.hbank.api.request.RegisterUserRequest
import org.springframework.stereotype.Component
import java.time.Clock

@Component
class UserMapper(private val clock: Clock) {

    fun toEntity(request: RegisterUserRequest): User = User(
        email = request.email,
        username = request.username,
        password = "",
        createdAt = clock.instant(),
        modifiedAt = clock.instant()
    )
}
package org.example.hbank.api.repository

import org.example.hbank.api.model.Token
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TokenRepository : JpaRepository<Token, UUID> {

    fun existsTokenByValue(value: String): Boolean
}
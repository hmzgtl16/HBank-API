package org.example.hbank.api.repository

import org.example.hbank.api.model.TransactionToken
import org.example.hbank.api.model.TransactionTokenId
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionTokenRepository : JpaRepository<TransactionToken, TransactionTokenId>
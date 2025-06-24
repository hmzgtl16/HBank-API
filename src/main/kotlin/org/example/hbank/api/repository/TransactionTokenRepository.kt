package org.example.hbank.api.repository

import org.example.hbank.api.model.Transaction
import org.example.hbank.api.model.TransactionToken
import org.example.hbank.api.model.TransactionTokenId
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TransactionTokenRepository : JpaRepository<TransactionToken, TransactionTokenId> {

    fun findTransactionTokensByTokenValue(value: String): TransactionToken?

    fun deleteTransactionTokensByTransactionId(id: UUID)
}

package org.example.hbank.api.service

import org.example.hbank.api.mapper.TransactionMapper
import org.example.hbank.api.repository.AccountRepository
import org.example.hbank.api.repository.TransactionRepository
import org.example.hbank.api.response.TransactionResponse
import org.example.hbank.api.util.AccountNotFoundException
import org.example.hbank.api.util.TransactionNotFoundException
import org.example.hbank.api.util.TransactionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface TransactionService {
    fun getTransactions(username: String, pageable: Pageable): Page<TransactionResponse>
    fun getTransaction(reference: UUID): TransactionResponse
}

@Service
class TransactionServiceImpl(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionMapper: TransactionMapper
) : TransactionService {

    @Transactional
    override fun getTransactions(username: String, pageable: Pageable): Page<TransactionResponse> {
        if (!accountRepository.existsAccountByCustomerUserUsername(username = username))
            throw AccountNotFoundException()
        return transactionRepository.findDistinctTransactionsByFromCustomerUserUsernameOrToCustomerUserUsernameAndStatusIn(
            username = username,
            status = listOf(TransactionStatus.COMPLETED),
            pageable = pageable
        ).map(transactionMapper::toResponse)
    }

    override fun getTransaction(reference: UUID): TransactionResponse {
        val transaction = transactionRepository.findTransactionByReference(reference = reference)
            ?: throw TransactionNotFoundException()
        return transactionMapper.toResponse(transaction)
    }
}

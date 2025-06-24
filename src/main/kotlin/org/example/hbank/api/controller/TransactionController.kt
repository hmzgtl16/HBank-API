package org.example.hbank.api.controller

import org.example.hbank.api.response.TransactionResponse
import org.example.hbank.api.service.TransactionService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["api/v1/transactions"])
class TransactionController(
    private val transactionService: TransactionService
) {

    @PostMapping
    fun getTransactions(
        @PageableDefault(size = 20) pageable: Pageable,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<Page<TransactionResponse>> {
        val response = transactionService
            .getTransactions(username = user.username, pageable = pageable)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}


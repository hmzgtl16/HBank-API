package org.example.hbank.api.controller

import jakarta.validation.Valid
import org.example.hbank.api.request.ConfirmTransferRequest
import org.example.hbank.api.request.CreateTransferRequest
import org.example.hbank.api.request.VerifyTransferRequest
import org.example.hbank.api.response.TransactionResponse
import org.example.hbank.api.service.TransferService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["api/v1/transaction/transfer"])
class TransferController(
    private val transferService: TransferService
) {

    @PostMapping
    fun createTransfer(
        @Valid @RequestBody createTransferRequest: CreateTransferRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<TransactionResponse> {
        val response = transferService.createTransfer(request = createTransferRequest, username = user.username)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("verify")
    fun verifyTransfer(
        @Valid @RequestBody verifyTransferRequest: VerifyTransferRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<Unit> {
        transferService.verifyTransfer(username = user.username, request = verifyTransferRequest)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @PostMapping("confirm")
    fun confirmTransfer(
        @Valid @RequestBody confirmTransferRequest: ConfirmTransferRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<TransactionResponse> {
        val response =
            transferService.confirmTransfer(username = user.username, request = confirmTransferRequest)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}


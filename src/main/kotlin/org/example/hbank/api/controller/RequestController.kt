package org.example.hbank.api.controller

import jakarta.validation.Valid
import org.example.hbank.api.request.*
import org.example.hbank.api.response.TransactionResponse
import org.example.hbank.api.service.RequestService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["api/v1/transaction/request"])
class RequestController(
    private val requestService: RequestService
) {

    @PostMapping
    fun createRequest(
        @Valid @RequestBody createRequestRequest: CreateRequestRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<TransactionResponse> {
        val response = requestService.createRequest(request = createRequestRequest, username = user.username)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("accept")
    fun acceptRequest(
        @Valid @RequestBody acceptRequestRequest: AcceptRequestRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<TransactionResponse> {
        val response = requestService.acceptRequest(request = acceptRequestRequest, username = user.username)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping("decline")
    fun declineRequest(
        @Valid @RequestBody declineRequestRequest: DeclineRequestRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<TransactionResponse> {
        val response = requestService.declineRequest(request = declineRequestRequest, username = user.username)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping("verify")
    fun verifyRequest(
        @Valid @RequestBody verifyRequestRequest: VerifyRequestRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<Unit> {
        requestService.verifyRequest(request = verifyRequestRequest, username = user.username)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @PostMapping("confirm")
    fun confirmRequest(
        @Valid @RequestBody confirmRequestRequest: ConfirmRequestRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<TransactionResponse> {
        val response = requestService.confirmRequest(request = confirmRequestRequest, username = user.username)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping("cancel")
    fun cancelRequest(
        @Valid @RequestBody cancelRequestRequest: CancelRequestRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<TransactionResponse> {
        val response = requestService.cancelRequest(request = cancelRequestRequest, username = user.username)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}



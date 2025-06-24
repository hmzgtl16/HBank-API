package org.example.hbank.api.controller

import jakarta.validation.constraints.Pattern
import org.example.hbank.api.request.UpdateCustomerAddressRequest
import org.example.hbank.api.response.CustomerResponse
import org.example.hbank.api.service.CustomerService
import org.example.hbank.api.util.Errors
import org.example.hbank.api.util.Validator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(path = ["api/v1/customers"])
class CustomerController(private val customerService: CustomerService) {

    @GetMapping
    fun getCustomerPersonalInfo(@AuthenticationPrincipal user: UserDetails): ResponseEntity<CustomerResponse> =
        ResponseEntity.ok(customerService.getCustomer(username = user.username))

    @GetMapping("avatar/{id}")
    fun getCustomerAvatar(
        @PathVariable(name = "id")
        @Pattern(regexp = Validator.UUID_REGEX, message = Errors.UUID_INVALID_FORMAT)
        id: String
    ): ResponseEntity<ByteArray> =
        ResponseEntity.ok(customerService.getCustomerAvatar(id = id))

    @PostMapping("avatar/update")
    fun updateCustomerAvatar(
        @RequestParam() avatar: MultipartFile,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<Nothing> {
        customerService.updateCustomerAvatar(avatar = avatar, username = user.username)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @PostMapping("address/update")
    fun updateCustomerAddress(
        @RequestBody request: UpdateCustomerAddressRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<Nothing> {
        customerService.updateCustomerAddress(request = request, username = user.username)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    /*  @PostMapping( "outdated")
      fun isOutdated(
          @AuthenticationPrincipal user: UserDetails,
          @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          modified: Instant
      ): ResponseEntity<Boolean> =
          ResponseEntity.status(HttpStatus.OK).body(customerService.isOutdated(user = user, modified = modified))*/
}

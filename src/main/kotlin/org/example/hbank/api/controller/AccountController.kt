package org.example.hbank.api.controller

import org.example.hbank.api.response.PersonalAccountResponse
import org.example.hbank.api.service.AccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["api/v1/accounts"])
class AccountController(
    private val accountService: AccountService
) {

    @GetMapping("personal")
    fun getPersonalAccount(@AuthenticationPrincipal user: UserDetails): ResponseEntity<PersonalAccountResponse> {
        val response = accountService.getPersonalAccount(username = user.username)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    /* @PostMapping(
         path = ["personal/outdated"],
         consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
         produces = [MediaType.APPLICATION_JSON_VALUE]
     )
     fun isOutdated(
         @RequestParam(name = "modified")
         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
         modified: Instant,
     ): ResponseEntity<Boolean> {

         val isOutdated = transactionTemplate.execute {

             val user = userService.getAuthenticatedUser()
                 ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.USER_NOT_FOUND)

             val customer = customerService.getCustomerByUser(user = user)
                 ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.CUSTOMER_NOT_FOUND)

             val account = accountService.getCustomerPersonalAccount(customer = customer)
                 ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.PERSONAL_ACCOUNT_NOT_FOUND)

             account.modified.isAfter(modified)
         } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.PERSONAL_ACCOUNT_NOT_FOUND)

         return ResponseEntity.ok(isOutdated)
     }*/
}

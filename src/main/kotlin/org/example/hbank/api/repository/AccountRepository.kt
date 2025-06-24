package org.example.hbank.api.repository

import org.example.hbank.api.model.Account
import org.example.hbank.api.util.AccountType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AccountRepository : JpaRepository<Account, UUID> {

    fun findAccountByNumber(number: String): Account?
    fun findAccountByCustomerUserUsername(username: String): Account?
    fun findAccountByCustomerUserUsernameAndType(username: String, type: AccountType): Account?
    fun findAccountByCustomerUserEmailAndType(email: String, type: AccountType): Account?
    fun findAccountByCustomerUserPhoneNumberAndType(phoneNumber: String, type: AccountType): Account?

    fun existsAccountByNumber(number: String): Boolean
    fun existsAccountByCustomerUserUsername(username: String): Boolean
}
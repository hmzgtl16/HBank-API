package org.example.hbank.api.repository

import org.example.hbank.api.utility.AccountType
import org.example.hbank.api.model.Account
import org.example.hbank.api.model.Customer
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AccountRepository : JpaRepository<Account, UUID> {

    fun findAccountByNumber(number: String): Account?
    fun findAccountByCustomerAndType(customer: Customer, type: AccountType): Account?

    fun existsAccountByNumber(number: String): Boolean
}
package org.example.hbank.api.repository

import org.example.hbank.api.model.Customer
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CustomerRepository : JpaRepository<Customer, UUID> {

    fun findCustomerByUserUsername(username: String): Customer?
    fun findCustomerByUserEmail(email: String): Customer?
    fun findCustomerByUserPhoneNumber(phoneNumber: String): Customer?
}
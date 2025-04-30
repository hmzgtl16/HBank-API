package org.example.hbank.api.repository

import org.example.hbank.api.model.Customer
import org.example.hbank.api.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CustomerRepository : JpaRepository<Customer, UUID> {

    fun findCustomerByUser(user: User) : Customer?
}
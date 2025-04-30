package org.example.hbank.api.service

import org.example.hbank.api.model.Address
import org.example.hbank.api.model.Customer
import org.example.hbank.api.model.File
import org.example.hbank.api.model.User
import org.example.hbank.api.repository.CustomerRepository
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant

@Service
class CustomerService(
    private val clock: Clock,
    private val customerRepository: CustomerRepository
) {

    fun createConsumer(user: User): Customer {
        val customer = Customer(
            created = Instant.now(clock),
            modified = Instant.now(clock),
            user = user
        )

        return customerRepository.save(customer)
    }

    fun updateCustomerAddress(
        customer: Customer,
        address: Address
    ): Customer {
        customer.also {
            it.modified = Instant.now(clock)
            it.address = address
        }

        return customerRepository.save(customer)
    }

    fun updateCustomerAvatar(customer: Customer, avatar: File) {
        customer.also {
            it.modified = Instant.now(clock)
            it.avatar = avatar
        }

        customerRepository.save(customer)
    }

    fun getCustomerByUser(user: User): Customer? =
        customerRepository.findCustomerByUser(user = user)

}


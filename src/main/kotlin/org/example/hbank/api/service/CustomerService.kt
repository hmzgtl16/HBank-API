package org.example.hbank.api.service

import org.example.hbank.api.mapper.AddressMapper
import org.example.hbank.api.mapper.CustomerMapper
import org.example.hbank.api.mapper.FileMapper
import org.example.hbank.api.repository.AddressRepository
import org.example.hbank.api.repository.CustomerRepository
import org.example.hbank.api.repository.FileRepository
import org.example.hbank.api.request.UpdateCustomerAddressRequest
import org.example.hbank.api.response.CustomerResponse
import org.example.hbank.api.util.CustomerNotFoundException
import org.example.hbank.api.util.Errors
import org.example.hbank.api.util.FileNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.time.Clock
import java.util.*

interface CustomerService {
    fun getCustomer(username: String): CustomerResponse
    fun getCustomerAvatar(id: String): ByteArray
    fun updateCustomerAvatar(avatar: MultipartFile, username: String)
    fun updateCustomerAddress(request: UpdateCustomerAddressRequest, username: String)
    //fun isOutdated(user: UserDetails, modified: Instant): Boolean
}

@Service
class CustomerServiceImpl(
    private val clock: Clock,
    private val addressRepository: AddressRepository,
    private val customerRepository: CustomerRepository,
    private val fileRepository: FileRepository,
    private val addressMapper: AddressMapper,
    private val customerMapper: CustomerMapper,
    private val fileMapper: FileMapper
) : CustomerService {

    @Transactional
    override fun getCustomer(username: String): CustomerResponse {
        val customer = customerRepository.findCustomerByUserUsername(username = username)
            ?: throw CustomerNotFoundException()

        return customerMapper.toResponse(customer = customer)
    }

    @Transactional
    override fun getCustomerAvatar(id: String): ByteArray {
        val file = fileRepository.findFileById(id = UUID.fromString(id))
            ?: throw FileNotFoundException()

        return file.data
    }

    @Transactional
    override fun updateCustomerAvatar(avatar: MultipartFile, username: String) {
        val customer = customerRepository.findCustomerByUserUsername(username = username)
            ?: throw CustomerNotFoundException()

        val file = fileRepository.save(fileMapper.toEntity(multipartFile = avatar))

        customerRepository.save(customerMapper.updateEntity(customer = customer, avatar = file))
    }

    @Transactional
    override fun updateCustomerAddress(request: UpdateCustomerAddressRequest, username: String) {
        val customer = customerRepository.findCustomerByUserUsername(username = username)
            ?: throw CustomerNotFoundException()

        val address = addressRepository.save(addressMapper.toEntity(request = request))

        customerRepository.save(customerMapper.updateEntity(customer = customer, address = address))
    }

    /*
    @Transactional
    override fun isOutdated(user: UserDetails, modified: Instant): Boolean {
        val customer = customerRepository.findCustomerByUserUsername(username = user.username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, Errors.CUSTOMER_NOT_FOUND)

        return customer.modified.isAfter(modified)
    }
    */
}


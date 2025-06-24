package org.example.hbank.api.service

import com.waseetpay.api.response.AddressResponse
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toKotlinLocalDate
import org.example.hbank.api.mapper.AddressMapper
import org.example.hbank.api.mapper.CustomerMapper
import org.example.hbank.api.mapper.FileMapper
import org.example.hbank.api.model.Customer
import org.example.hbank.api.repository.AddressRepository
import org.example.hbank.api.repository.CustomerRepository
import org.example.hbank.api.repository.FileRepository
import org.example.hbank.api.request.UpdateCustomerAddressRequest
import org.example.hbank.api.response.CustomerResponse
import org.example.hbank.api.util.CustomerNotFoundException
import org.example.hbank.api.util.FileNotFoundException
import org.example.hbank.api.util.TestDataFactory
import org.example.hbank.api.util.TestResponseFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.willReturn
import org.springframework.mock.web.MockMultipartFile
import org.springframework.util.StringUtils
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.time.Clock
import java.util.*
import kotlin.test.assertContentEquals

class CustomerServiceTest {

    private val clock = mock<Clock>()
    private val addressRepository = mock<AddressRepository>()
    private val customerRepository = mock<CustomerRepository>()
    private val fileRepository = mock<FileRepository>()
    private val addressMapper = mock<AddressMapper>()
    private val customerMapper = mock<CustomerMapper>()
    private val fileMapper = mock<FileMapper>()

    private val customerService: CustomerService = CustomerServiceImpl(
        clock = clock,
        addressRepository = addressRepository,
        customerRepository = customerRepository,
        fileRepository = fileRepository,
        addressMapper = addressMapper,
        customerMapper = customerMapper,
        fileMapper = fileMapper
    )

    @Test
    fun `should get customer by username successfully`() {
        // given
        val username = "test_username"
        val user = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val customer = TestDataFactory.createCustomer(id = UUID.randomUUID(), user = user)

        given(methodCall = customerRepository.findCustomerByUserUsername(username = username))
            .willReturn(customer)
        given(methodCall = customerMapper.toResponse(customer = customer))
            .willReturn(TestResponseFactory.toCustomerResponse(customer = customer))

        // when
        val result = customerService.getCustomer(username = username)

        // then
        verify(mock = customerRepository).findCustomerByUserUsername(username = username)

        assert(value = result.firstname == customer.firstname)
        assert(value = result.lastname == customer.lastname)
        assert(value = result.username == customer.user.username)
        assert(value = result.email == customer.user.email)
    }

    @Test
    fun `should throw exception when non-existent customer`() {
        // given
        val username = "test_username"

        given(methodCall = customerRepository.findCustomerByUserUsername(username = username)).willReturn(null)

        // then
        assertThrows<CustomerNotFoundException> {
            customerService.getCustomer(username = username)
        }
    }

    @Test
    fun `should get customer avatar by id successfully`() {
        // given
        val fileId = UUID.randomUUID()
        val avatar = TestDataFactory.createFile(id = fileId)

        given(methodCall = fileRepository.findFileById(id = fileId)).willReturn(avatar)

        // when
        val result = customerService.getCustomerAvatar(id = fileId.toString())

        // then
        verify(mock = fileRepository).findFileById(id = fileId)

        assertContentEquals(expected = avatar.data, actual = result)
    }

    @Test
    fun `should throw exception when non-existent avatar`() {
        // given
        val fileId = UUID.randomUUID()

        given(methodCall = fileRepository.findFileById(id = fileId)).willReturn(null)

        // then
        assertThrows<FileNotFoundException> {
            customerService.getCustomerAvatar(id = fileId.toString())
        }
    }

    @Test
    fun `should update the customer address successfully`() {
        // given
        val username = "test_username"
        val request = UpdateCustomerAddressRequest(
            municipality = "New Municipality",
            province = "NewProvince",
            street = "New Street",
            zip = "New ZIP"
        )
        val user = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val existingAddress = TestDataFactory.createAddress(id = UUID.randomUUID())
        val existingCustomer = TestDataFactory
            .createCustomer(id = UUID.randomUUID(), address = existingAddress, user = user)

        val updatedAddress = existingAddress.copy(
            municipality = request.municipality,
            province = request.province,
            street = request.street,
            zip = request.zip
        )
        val updatedCustomer = existingCustomer.copy(address = existingAddress)

        given(methodCall = customerRepository.findCustomerByUserUsername(username = username))
            .willReturn(existingCustomer)
        given(methodCall = addressMapper.toEntity(request = request)).willReturn(updatedAddress)
        given(methodCall = addressRepository.save(updatedAddress)).willReturn(updatedAddress)
        given(methodCall = customerMapper.updateEntity(customer = existingCustomer, address = updatedAddress))
            .willReturn(updatedCustomer)
        given(customerRepository.save(updatedCustomer)).willReturn(updatedCustomer)

        // when
        customerService.updateCustomerAddress(request = request, username = username)

        // then
        verify(mock = customerRepository).findCustomerByUserUsername(username = username)
        verify(mock = addressRepository).save(updatedAddress)
        verify(mock = customerRepository).save(updatedCustomer)
    }

    @Test
    fun `should throw exception when updating non-existent customer address`() {
        // given
        val username = "test_username"
        val request = UpdateCustomerAddressRequest(
            municipality = "New Municipality",
            province = "NewProvince",
            street = "New Street",
            zip = "New ZIP"
        )

        given(methodCall = customerRepository.findCustomerByUserUsername(username = username)).willReturn(null)

        // when/then
        assertThrows<CustomerNotFoundException> {
            customerService.updateCustomerAddress(request = request, username = username)
        }

        verify(mock = customerRepository).findCustomerByUserUsername(username = username)
        verify(mock = addressRepository, mode = never()).save(any())
        verify(mock = customerRepository, mode = never()).save(any())
    }

    @Test
    fun `should update the customer avatar successfully`() {
        // given
        val username = "test_username"
        val avatar = MockMultipartFile(
            "NewTestFile",
            "TestFile",
            "image/png",
            byteArrayOf(1, 1, 1, 1, 1, 1)
        )
        val user = TestDataFactory.createUser(id = UUID.randomUUID(), username = username)
        val existingAvatar = TestDataFactory.createFile(id = UUID.randomUUID())
        val existingCustomer = TestDataFactory
            .createCustomer(id = UUID.randomUUID(), avatar = existingAvatar, user = user)

        val updatedAvatar = existingAvatar.copy(
            name = StringUtils.cleanPath(avatar.originalFilename),
            type = avatar.contentType.toString(),
            data = avatar.bytes
        )
        val updatedCustomer = existingCustomer.copy(avatar = updatedAvatar)

        given(methodCall = customerRepository.findCustomerByUserUsername(username = username))
            .willReturn(existingCustomer)
        given(methodCall = fileMapper.toEntity(multipartFile = avatar)).willReturn(updatedAvatar)
        given(methodCall = fileRepository.save(updatedAvatar)).willReturn(updatedAvatar)
        given(methodCall = customerMapper.updateEntity(customer = existingCustomer, avatar = updatedAvatar))
            .willReturn(updatedCustomer)
        given(customerRepository.save(updatedCustomer)).willReturn(updatedCustomer)

        // when
        customerService.updateCustomerAvatar(avatar = avatar, username = username)

        // then
        verify(mock = customerRepository).findCustomerByUserUsername(username = username)
        verify(mock = fileRepository).save(updatedAvatar)
        verify(mock = customerRepository).save(updatedCustomer)
    }

    @Test
    fun `should throw exception when updating non-existent customer avatar`() {
        // given
        val username = "test_username"
        val avatar = MockMultipartFile(
            "NewTestFile",
            "TestFile",
            "image/png",
            byteArrayOf(1, 1, 1, 1, 1, 1)
        )

        given(methodCall = customerRepository.findCustomerByUserUsername(username = username)).willReturn(null)

        // when/then
        assertThrows<CustomerNotFoundException> {
            customerService.updateCustomerAvatar(avatar = avatar, username = username)
        }

        verify(mock = customerRepository).findCustomerByUserUsername(username = username)
        verify(mock = fileRepository, mode = never()).save(any())
        verify(mock = customerRepository, mode = never()).save(any())
    }
}
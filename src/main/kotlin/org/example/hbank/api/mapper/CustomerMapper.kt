package org.example.hbank.api.mapper

import com.waseetpay.api.response.AddressResponse
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toKotlinLocalDate
import org.example.hbank.api.model.Address
import org.example.hbank.api.model.Customer
import org.example.hbank.api.model.File
import org.example.hbank.api.model.User
import org.example.hbank.api.response.CustomerResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.time.Clock

@Component
class CustomerMapper(private val clock: Clock) {

    fun toEntity(user: User): Customer = Customer(
        createdAt = clock.instant(),
        modifiedAt = clock.instant(),
        user = user
    )

    fun toResponse(customer: Customer): CustomerResponse = CustomerResponse(
        firstname = customer.firstname,
        lastname = customer.lastname,
        birthdate = customer.birthdate?.toKotlinLocalDate(),
        username = customer.user.username,
        email = customer.user.email,
        phoneNumber = customer.user.phoneNumber,
        address = addressResponse(address = customer.address),
        avatar = avatarUrl(avatar = customer.avatar),
        modifiedAt = customer.modifiedAt.toKotlinInstant(),
        verified = customer.verified
    )

    fun updateEntity(
        address: Address? = null,
        avatar: File? = null,
        customer: Customer
    ): Customer = customer.copy(
        modifiedAt = clock.instant(),
        address = address,
        avatar = avatar
    )

    private fun avatarUrl(avatar: File?): String? = avatar?.let {
        ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/v1/customer/avatar")
            .path(it.id.toString())
            .toUriString()
    }

    private fun addressResponse(address: Address?): AddressResponse? = address?.let {
        AddressResponse(
            street = it.street,
            municipality = it.municipality,
            province = it.province,
            zip = it.zip
        )
    }
}


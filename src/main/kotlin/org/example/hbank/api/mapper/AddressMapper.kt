package org.example.hbank.api.mapper

import org.example.hbank.api.model.Address
import org.example.hbank.api.request.UpdateCustomerAddressRequest
import org.springframework.stereotype.Component

@Component
class AddressMapper {

    fun toEntity(request: UpdateCustomerAddressRequest): Address = Address(
        municipality = request.municipality,
        province = request.province,
        street = request.street,
        zip = request.zip
    )
}
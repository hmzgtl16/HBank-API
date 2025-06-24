package org.example.hbank.api.repository

import org.example.hbank.api.model.Address
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AddressRepository : JpaRepository<Address, UUID>
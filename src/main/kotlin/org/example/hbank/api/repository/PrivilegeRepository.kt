package org.example.hbank.api.repository

import org.example.hbank.api.model.Privilege
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PrivilegeRepository : JpaRepository<Privilege, UUID>
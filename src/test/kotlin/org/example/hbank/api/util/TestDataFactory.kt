package org.example.hbank.api.util

import org.example.hbank.api.model.*
import java.time.Instant
import java.time.LocalDate
import java.util.*

object TestDataFactory {

    fun createUser(
        id: UUID? = null,
        email: String = "test@example.com",
        username: String = "testusername",
        password: String = "password",
        phoneNumber: String = "+1234567890",
        enabled: Boolean = true,
        createdAt: Instant = Instant.now(),
        modifiedAt: Instant = Instant.now()
    ): User =
        User(
            id = id,
            email = email,
            username = username,
            password = password,
            phoneNumber = phoneNumber,
            enabled = enabled,
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )

    fun createCustomer(
        id: UUID? = null,
        firstname: String = "Test",
        lastname: String = "Customer",
        birthDate: LocalDate = LocalDate.of(2000, 1, 1),
        verified: Boolean = true,
        createdAt: Instant = Instant.now(),
        modifiedAt: Instant = Instant.now(),
        user: User,
        address: Address? = null,
        avatar: File? = null,
    ): Customer =
        Customer(
            id = id,
            firstname = firstname,
            lastname = lastname,
            birthdate = birthDate,
            verified = verified,
            createdAt = createdAt,
            modifiedAt = modifiedAt,
            user = user,
            address = address,
            avatar = avatar
        )

    fun createAccount(
        id: UUID? = null,
        number: String = "1234567890",
        name: String = "Test Account",
        balance: Double = 0.0,
        type: AccountType = AccountType.PERSONAL_ACCOUNT,
        limit: AccountLimit = AccountLimit.STANDARD_PERSONAL_ACCOUNT,
        status: AccountStatus = AccountStatus.ACTIVATED,
        createdAt: Instant = Instant.now(),
        modifiedAt: Instant = Instant.now(),
        customer: Customer,
    ): Account =
        Account(
            id = id,
            number = number,
            name = name,
            balance = balance,
            type = type,
            limit = limit,
            status = status,
            createdAt = createdAt,
            modifiedAt = modifiedAt,
            customer = customer
        )

    fun createToken(
        id: UUID? = null,
        value: String = "123456",
        createdAt: Instant = Instant.now()
    ): Token =
        Token(
            id = id,
            value = value,
            createdAt = createdAt
        )

    fun createAccountToken(
        account: Account,
        token: Token
    ): AccountToken =
        AccountToken(
            account = account,
            token = token
        )

    fun createFile(
        id: UUID? = null,
        name: String = "TestFile",
        type: String = "image/png",
        data: ByteArray = byteArrayOf(1, 1, 1, 1, 1, 1)
    ): File =
        File(
            id = id,
            name = name,
            type = type,
            data = data
        )

    fun createAddress(
        id: UUID? = null,
        municipality: String = "TestMunicipality",
        province: String = "TestProvince",
        street: String = "TestStreet",
        zip: String = "TestZip",
    ): Address =
        Address(
            id = id,
            municipality = municipality,
            province = province,
            street = street,
            zip = zip
        )

    fun createRole(
        id: UUID? = null,
        name: String
    ): Role =
        Role(
            id = id,
            name = name,
        )

    fun createUserRole(
        user: User,
        role: Role
    ): UserRole =
        UserRole(
            user = user,
            role = role
        )

    fun createUserToken(
        user: User,
        token: Token,
        type: TokenType
    ): UserToken =
        UserToken(
            user = user,
            token = token,
            type = type
        )

    fun createTransaction(
        id: UUID? = null,
        reference: UUID = UUID.randomUUID(),
        amount: Double = 239.99,
        type: TransactionType = TransactionType.TRANSFER,
        status: TransactionStatus = TransactionStatus.COMPLETED,
        createdAt: Instant = Instant.now(),
        modifiedAt: Instant = Instant.now(),
        from: Account,
        to: Account,
    ): Transaction =
        Transaction(
            id = id,
            reference = reference,
            amount = amount,
            type = type,
            status = status,
            createdAt = createdAt,
            modifiedAt = modifiedAt,
            from = from,
            to = to
        )

    fun createTransactionToken(
        transaction: Transaction,
        token: Token
    ): TransactionToken =
        TransactionToken(
            transaction = transaction,
            token = token
        )
}
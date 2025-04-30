package org.example.hbank.api.utility

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = AccountLimitSerializer::class)
enum class AccountLimit(
    val minimumTransactionAmount: Double = 0.0,
    val maximumTransactionAmount: Double = Double.MAX_VALUE,
    val dailyTransfersAmountLimit: Double = Double.MAX_VALUE,
    val monthlyTransfersAmountLimit: Double = Double.MAX_VALUE,
    val yearlyTransfersAmountLimit: Double = Double.MAX_VALUE,
    val maximumDailyTransferNumber: Int = Int.MAX_VALUE,
    val maximumMonthlyTransferNumber: Int = Int.MAX_VALUE,
    val maximumYearlyTransferNumber: Int = Int.MAX_VALUE,
    val dailyRequestsAmountLimit: Double = Double.MAX_VALUE,
    val monthlyRequestsAmountLimit: Double = Double.MAX_VALUE,
    val yearlyRequestsAmountLimit: Double = Double.MAX_VALUE,
    val maximumDailyRequestNumber: Int = Int.MAX_VALUE,
    val maximumMonthlyRequestNumber: Int = Int.MAX_VALUE,
    val maximumYearlyRequestNumber: Int = Int.MAX_VALUE,
) {

    STANDARD_PERSONAL_ACCOUNT(
        minimumTransactionAmount = 100.00,
        maximumTransactionAmount = 50000.00,
        dailyTransfersAmountLimit = 50000.00,
        monthlyTransfersAmountLimit = 200000.00,
        yearlyTransfersAmountLimit = 2400000.00,
        maximumDailyTransferNumber = 3,
        maximumMonthlyTransferNumber = 30,
        maximumYearlyTransferNumber = 360,
        dailyRequestsAmountLimit = 50000.00,
        monthlyRequestsAmountLimit = 200000.00,
        yearlyRequestsAmountLimit = 500000.00,
        maximumDailyRequestNumber = 1,
        maximumMonthlyRequestNumber = 3,
        maximumYearlyRequestNumber = 10
    ),

    PREMIUM_PERSONAL_ACCOUNT(
        minimumTransactionAmount = 100.00,
        maximumTransactionAmount = 100000.00,
        dailyTransfersAmountLimit = 100000.00,
        monthlyTransfersAmountLimit = 500000.00,
        yearlyTransfersAmountLimit = 2500000.00,
        maximumDailyTransferNumber = 10,
        maximumMonthlyTransferNumber = 50,
        maximumYearlyTransferNumber = 600,
        dailyRequestsAmountLimit = 50000.00,
        monthlyRequestsAmountLimit = 200000.00,
        yearlyRequestsAmountLimit = 500000.00,
        maximumDailyRequestNumber = 1,
        maximumMonthlyRequestNumber = 3,
        maximumYearlyRequestNumber = 10
    ),

    UNKNOWN(
        minimumTransactionAmount = 0.0,
        maximumTransactionAmount = 0.0,
        dailyTransfersAmountLimit = 0.0,
        monthlyTransfersAmountLimit = 0.0,
        yearlyTransfersAmountLimit = 0.0,
        maximumDailyTransferNumber = 0,
        maximumMonthlyTransferNumber = 0,
        maximumYearlyTransferNumber = 0,
        dailyRequestsAmountLimit = 0.0,
        monthlyRequestsAmountLimit = 0.0,
        yearlyRequestsAmountLimit = 0.0,
        maximumDailyRequestNumber = 0,
        maximumMonthlyRequestNumber = 0,
        maximumYearlyRequestNumber = 0,
    )
}

object AccountLimitSerializer : KSerializer<AccountLimit> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            serialName = "account limit",
            kind = PrimitiveKind.INT,
        )

    override fun deserialize(decoder: Decoder): AccountLimit =
        decoder.decodeInt().asAccountLimit()

    override fun serialize(encoder: Encoder, value: AccountLimit) {
        encoder.encodeInt(value = value.ordinal)
    }
}

private fun Int.asAccountLimit(): AccountLimit =
    AccountLimit.entries.firstOrNull { this == it.ordinal } ?: AccountLimit.UNKNOWN
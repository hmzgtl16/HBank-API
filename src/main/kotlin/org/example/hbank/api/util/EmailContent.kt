package org.example.hbank.api.util

import jakarta.mail.internet.InternetAddress
import java.time.Instant

sealed class EmailContent(
    val to: InternetAddress,
    val variables: Map<String, String>,
    val template: EmailTemplate
) {

    class VerifyEmail(to: String, username: String, token: String) : EmailContent(
        to = InternetAddress(to),
        variables = mapOf(USERNAME_VARIABLE to username, TOKEN_VARIABLE to token),
        template = EmailTemplate.VERIFY_EMAIL
    )

    class ForgotPassword(to: String, username: String, token: String) : EmailContent(
        to = InternetAddress(to),
        variables = mapOf(USERNAME_VARIABLE to username, TOKEN_VARIABLE to token),
        template = EmailTemplate.FORGOT_PASSWORD
    )

    class VerifyTransfer(to: String, username: String, token: String) : EmailContent(
        to = InternetAddress(to),
        variables = mapOf(USERNAME_VARIABLE to username, TOKEN_VARIABLE to token),
        template = EmailTemplate.VERIFY_TRANSFER
    )

    class SuccessTransfer(
        to: String,
        username: String,
        amount: Double,
        recipient: String,
        account: String,
        time: Instant
    ) : EmailContent(
        to = InternetAddress(to),
        variables = mapOf(
            USERNAME_VARIABLE to username,
            AMOUNT_VARIABLE to amount.toString(),
            ACCOUNT_VARIABLE to account,
            RECIPIENT_VARIABLE to recipient,
            TIME_VARIABLE to time.toString()
        ),
        template = EmailTemplate.SUCCESS_TRANSFER
    )

    class ReceivedTransfer(
        to: String,
        username: String,
        amount: Double,
        account: String,
        time: Instant
    ) : EmailContent(
        to = InternetAddress(to),
        variables = mapOf(
            USERNAME_VARIABLE to username,
            AMOUNT_VARIABLE to amount.toString(),
            ACCOUNT_VARIABLE to account,
            TIME_VARIABLE to time.toString()
        ),
        template = EmailTemplate.RECEIVED_TRANSFER
    )

    class ReceivedRequest(to: String, username: String, amount: Double, account: String) : EmailContent(
        to = InternetAddress(to),
        variables = mapOf(USERNAME_VARIABLE to username, AMOUNT_VARIABLE to amount.toString(), ACCOUNT_VARIABLE to account),
        template = EmailTemplate.RECEIVED_REQUEST
    )

    class VerifyRequest(to: String, username: String, token: String) : EmailContent(
        to = InternetAddress(to),
        variables = mapOf(USERNAME_VARIABLE to username, TOKEN_VARIABLE to token),
        template = EmailTemplate.VERIFY_REQUEST
    )

    class AcceptedRequest(to: String, username: String, recipient: String) : EmailContent(
        to = InternetAddress(to),
        variables = mapOf(USERNAME_VARIABLE to username, RECIPIENT_VARIABLE to recipient),
        template = EmailTemplate.ACCEPTED_REQUEST
    )

    class DeclinedRequest(to: String, username: String, recipient: String) : EmailContent(
        to = InternetAddress(to),
        variables = mapOf(USERNAME_VARIABLE to username, RECIPIENT_VARIABLE to recipient),
        template = EmailTemplate.DECLINED_REQUEST
    )

    companion object {

        private const val USERNAME_VARIABLE = "username"
        private const val TOKEN_VARIABLE = "token"
        private const val AMOUNT_VARIABLE = "amount"
        private const val RECIPIENT_VARIABLE = "recipient"
        private const val ACCOUNT_VARIABLE = "account"
        private const val TIME_VARIABLE = "time"
    }
}
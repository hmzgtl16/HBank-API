package org.example.hbank.api.util

import jakarta.mail.internet.InternetAddress

enum class EmailTemplate(
    val from: InternetAddress = InternetAddress("no-reply@hbank.com", "HBank"),
    val template: String,
    val subject: String
) {

    VERIFY_EMAIL(template = "verify-email", subject = "Verify your Email on HBank"),
    FORGOT_PASSWORD(template = "forgot-password", subject = "Reset password of your account on HBank"),
    VERIFY_TRANSFER(template = "verify-transfer", subject = "Verify Transfer on HBank"),
    SUCCESS_TRANSFER(template = "success-transfer", subject = "Receipt of Transfer on HBank"),
    RECEIVED_TRANSFER(template = "received-transfer", subject = "Received Transfer on HBank"),
    RECEIVED_REQUEST(template = "received-request", subject = "Received Request on HBank"),
    VERIFY_REQUEST(template = "verify-request", subject = "Verify Request on HBank"),
    ACCEPTED_REQUEST(template = "accepted-request", subject = "Accepted Request on HBank"),
    DECLINED_REQUEST(template = "declined-request", subject = "Declined Request on HBank"),
    CANCELLED_REQUEST(template = "cancelled-request", subject = "Cancelled Request on HBank"),
}

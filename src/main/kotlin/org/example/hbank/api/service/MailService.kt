package org.example.hbank.api.service

import jakarta.mail.internet.InternetAddress
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class MailService(
    private val templateEngine: TemplateEngine,
    private val javaMailSender: JavaMailSender
) {

    @Async
    fun sendVerifyEmailEmail(username: String, email: String, token: String) {
        javaMailSender.apply {
            createMimeMessage().let {
                MimeMessageHelper(it, Charsets.UTF_8.name()).also { helper ->
                    helper.setFrom(InternetAddress("no-reply@waseet-pay.com", "WaseetPay"))
                    helper.setTo(InternetAddress(email))
                    helper.setSubject("Verify your Email on WaseetPay")
                    helper.setText(buildVerifyEmailEmail(username, token), true)
                }
                send(it)
            }
        }
    }

    @Async
    fun sendForgotPasswordEmail(username: String, email: String, token: String) {
        javaMailSender.apply {
            createMimeMessage().let {
                MimeMessageHelper(it, Charsets.UTF_8.name()).also { helper ->
                    helper.setFrom(InternetAddress("no-reply@waseet-pay.com", "WaseetPay"))
                    helper.setTo(InternetAddress(email))
                    helper.setSubject("Reset password of your account on WaseetPay")
                    helper.setText(buildForgotPasswordEmail(username, token), true)
                }
                send(it)
            }
        }
    }

    @Async
    fun sendVerifyTransferEmail(username: String, email: String, token: String) {
        javaMailSender.apply {
            createMimeMessage().let {
                MimeMessageHelper(it, Charsets.UTF_8.name()).also { helper ->
                    helper.setFrom(InternetAddress("no-reply@waseet-pay.com", "WaseetPay"))
                    helper.setTo(InternetAddress(email))
                    helper.setSubject("Verify Transfer on WaseetPay")
                    helper.setText(buildVerifyTransferEmail(username, token), true)
                }
                send(it)
            }
        }
    }

    @Async
    fun sendVerifyRequestEmail(username: String, email: String, token: String) {
        javaMailSender.apply {
            createMimeMessage().let {
                MimeMessageHelper(it, Charsets.UTF_8.name()).also { helper ->
                    helper.setFrom(InternetAddress("no-reply@waseet-pay.com", "WaseetPay"))
                    helper.setTo(InternetAddress(email))
                    helper.setSubject("Verify Request on WaseetPay")
                    helper.setText(buildVerifyRequestEmail(username, token), true)
                }
                send(it)
            }
        }
    }

    private fun buildVerifyEmailEmail(username: String, token: String): String {
        val context = Context()
        context.setVariable("username", username)
        context.setVariable("token", token)
        return templateEngine.process("verify-email", context)
    }

    private fun buildForgotPasswordEmail(username: String, token: String): String {
        val context = Context()
        context.setVariable("username", username)
        context.setVariable("token", token)
        return templateEngine.process("forgot-password", context)
    }

    private fun buildVerifyTransferEmail(username: String, token: String): String {
        val context = Context()
        context.setVariable("username", username)
        context.setVariable("token", token)
        return templateEngine.process("verify_transfer", context)
    }

    private fun buildVerifyRequestEmail(username: String, token: String): String {
        val context = Context()
        context.setVariable("username", username)
        context.setVariable("token", token)
        return templateEngine.process("verify_request", context)
    }
}

package org.example.hbank.api.service

import jakarta.mail.internet.MimeMessage
import org.example.hbank.api.util.EmailContent
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

interface MailSender {
    fun sendEmail(email: EmailContent)
}

@Component
class MailSenderImpl(
    private val templateEngine: TemplateEngine,
    private val javaMailSender: JavaMailSender,
    private val mimeMessage: MimeMessage
) : MailSender {

    @Async
    override fun sendEmail(email: EmailContent) {
        MimeMessageHelper(mimeMessage).apply {
            setFrom(email.template.from)
            setTo(email.to)
            setSubject(email.template.subject)
            setText(
                templateEngine.process(
                    email.template.template,
                    Context(null, email.variables)
                ),
                true
            )
        }

        javaMailSender.send(mimeMessage)
    }
}


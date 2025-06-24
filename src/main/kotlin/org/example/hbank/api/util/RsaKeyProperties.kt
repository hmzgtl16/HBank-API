package org.example.hbank.api.util

import org.springframework.boot.context.properties.ConfigurationProperties
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@ConfigurationProperties(prefix = "application.jwt.rsa")
data class RsaKeyProperties(
    val publicKey: RSAPublicKey,
    val privateKey: RSAPrivateKey
)

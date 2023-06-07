package com.cchunduri.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import java.util.*

class JwtUtils(
    environment: ApplicationEnvironment
) {
    private val secret = environment.config.property("jwt.secret").getString()
    private val issuer = environment.config.property("jwt.issuer").getString()
    private val validityInMs = environment.config.property("jwt.validity").getString().toLong()

    fun makeToken(email: String): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("userId", email)
        .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
        .sign(Algorithm.HMAC256(secret))
}
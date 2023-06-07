package com.cchunduri.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuthentication() {
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()

    val verifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(secret))
        .withIssuer(issuer)
        .build()

    install(Authentication) {
        jwt("jwt") {
            verifier(verifier)
            validate {
                val userId = it.payload.getClaim("userId").asString()
                if (userId != null) {
                    JWTPrincipal(it.payload)
                } else {
                    null
                }
            }
        }
    }
}


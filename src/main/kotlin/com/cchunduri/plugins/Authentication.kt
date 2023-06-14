package com.cchunduri.plugins

import com.cchunduri.utils.JWT_CLAIM_USER_EMAIL
import com.cchunduri.utils.JwtUtils
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuthentication() {
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val validityInMs = environment.config.property("jwt.validity").getString().toLong()


    val jwtUtils = JwtUtils(environment)

    install(Authentication) {
        jwt("jwt") {
            verifier(jwtUtils.getJwtVerifier(secret, issuer))
            validate {
                val userId = getUserIdFromJwt(it)
                if (userId != null) {
                    JWTPrincipal(it.payload)
                } else {
                    null
                }
            }
        }
    }
}

fun getUserIdFromJwt(it: JWTCredential): String? =
    it.payload.getClaim(JWT_CLAIM_USER_EMAIL).asString()


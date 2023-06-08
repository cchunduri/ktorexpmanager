package com.cchunduri.plugins

import io.ktor.http.ContentType
import io.ktor.serialization.jackson.jackson
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    val converter = KotlinxSerializationConverter(Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    })
    install(ContentNegotiation) {
        json()
        jackson()
        register(ContentType.Application.Json, converter)
    }
    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

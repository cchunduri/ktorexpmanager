package com.cchunduri

import com.cchunduri.plugins.configureDatabases
import com.cchunduri.plugins.configureHTTP
import com.cchunduri.plugins.configureRouting
import com.cchunduri.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>): Unit =
    EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureRouting()
}

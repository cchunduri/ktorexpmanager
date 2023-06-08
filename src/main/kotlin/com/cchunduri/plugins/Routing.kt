package com.cchunduri.plugins

import com.cchunduri.dao.AppUser
import com.cchunduri.dao.Expense
import com.cchunduri.dto.LoginRequest
import com.cchunduri.services.ExpenseService
import com.cchunduri.utils.JwtUtils
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import java.lang.RuntimeException
import java.util.UUID

fun Application.configureRouting() {
    val database = connectToPostgres()
    val expenseService = ExpenseService(database)

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        authenticate("jwt") {
            route("/expenses") {
                get {
                    call.respond(expenseService.getAllExpenses())
                }

                post {
                    val newExpense = call.receive<Expense>()
                    val newExpenseId = expenseService.addExpense(newExpense).toString()
                    call.respond(HttpStatusCode.Created, newExpenseId)
                }
            }

            route("/expenses/{id}") {
                get {
                    val expenseId = call.parameters["id"]?.let { UUID.fromString(it) }

                    expenseId?.let { id ->
                        val expense = expenseService.getExpenseById(id)
                        expense?.let { expenseRes -> call.respond(expenseRes) } ?: call.respond(HttpStatusCode.NotFound)
                    } ?: call.respond(HttpStatusCode.BadRequest, "Invalid expense id")
                }

                delete {
                    val expenseId = call.parameters["id"]?.let { UUID.fromString(it) }

                    expenseId?.let { id ->
                        val isDeleted = expenseService.deleteExpense(id)
                        if (isDeleted)
                            call.respond(HttpStatusCode.NoContent)
                        else
                            call.respond(HttpStatusCode.NotFound)
                    } ?: call.respond(HttpStatusCode.BadRequest, "Invalid expense id")
                }
            }
        }

        val userService = UserService(database)
        route("/users") {
            post {
                val user = call.receive<AppUser>()
                userService.create(user)?.let {
                    call.respond(HttpStatusCode.Created, it.toString())
                } ?: call.respond(HttpStatusCode.Conflict, "User already exist")
            }
        }

        authenticate("jwt") {
            route("/users/{email}") {
                // Read user
                get {
                    val email = call.parameters["email"] ?: throw IllegalArgumentException("Invalid Email")
                    val user = userService.read(email)
                    if (user != null) {
                        call.respond(HttpStatusCode.OK, user)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }

                // Update user
                put {
                    val email = call.parameters["email"] ?: throw IllegalArgumentException("Invalid Email")
                    val user = call.receive<AppUser>()
                    userService.update(email, user)
                    call.respond(HttpStatusCode.OK)
                }

                // Delete user
                delete {
                    val email = call.parameters["email"] ?: throw IllegalArgumentException("Invalid Email")
                    userService.delete(email)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        val jwtUtils = environment?.let { JwtUtils(it) } ?: throw RuntimeException("Some thing happened")
        route("/login/") {
            post {
                val user = call.receive<LoginRequest>()

                val isValidLogin = userService.find(user.email, user.password)
                if (isValidLogin) {
                    val token = jwtUtils.makeToken(user.email)
                    call.respond(HttpStatusCode.OK, mapOf("token" to token))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
                }
            }
        }
    }
}



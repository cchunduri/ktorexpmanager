package com.cchunduri.plugins

import com.cchunduri.dao.Expense
import com.cchunduri.services.ExpenseService
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.receive
import java.util.UUID

fun Application.configureRouting() {
    val database = connectToPostgres()
    val expenseService: ExpenseService = ExpenseService(database)

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
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

}

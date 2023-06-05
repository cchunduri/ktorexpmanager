package com.cchunduri.services

import com.cchunduri.dao.Expense
import com.cchunduri.dao.Expenses
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class ExpenseService(
    private val database: Database
) {

    init {
        transaction(database) {
            SchemaUtils.create(Expenses)
        }
    }

    fun getAllExpenses(): List<Expense> = transaction {
        Expenses.selectAll().map {
            Expense(it[Expenses.id].value, it[Expenses.description], it[Expenses.amount], it[Expenses.category], it[Expenses.place], it[Expenses.time])
        }
    }

    fun getExpenseById(id: UUID): Expense? = transaction {
        Expenses.select { Expenses.id eq id }
            .singleOrNull()
            ?.let {
                Expense(it[Expenses.id].value, it[Expenses.description], it[Expenses.amount], it[Expenses.category], it[Expenses.place], it[Expenses.time])
            }
    }

    fun addExpense(
        newExpense: Expense
    ): UUID = transaction {
        Expenses.insertAndGetId {
            it[id] = UUID.randomUUID()
            it[description] = newExpense.description
            it[amount] = newExpense.amount
            it[category] = newExpense.category
            it[place] = newExpense.place
            it[time] = LocalDateTime.now()
        }.value
    }

    fun updateExpense(
        id: UUID,
        description: String,
        amount: Double,
        category: String,
        place: String,
        time: LocalDateTime
    ): Boolean = transaction {
        val updatedRowCount = Expenses.update({ Expenses.id eq id }) {
            it[Expenses.description] = description
            it[Expenses.amount] = amount
            it[Expenses.category] = category
            it[Expenses.place] = place
            it[Expenses.time] = time
        }
        updatedRowCount > 0
    }

    fun deleteExpense(id: UUID): Boolean = transaction {
        val deletedRowCount = Expenses.deleteWhere { Expenses.id eq id }
        deletedRowCount > 0
    }
}

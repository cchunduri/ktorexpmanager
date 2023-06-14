package com.cchunduri.plugins

import com.cchunduri.dao.AppUser
import com.cchunduri.dao.Expense
import com.cchunduri.dao.User
import com.cchunduri.dao.Users
import com.cchunduri.utils.PasswordUtils
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

class UserService(database: Database) {

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    fun create(user: User): UUID? = transaction {
        if (getUserByEmail(user.email).count() > 0) {
            return@transaction null
        }

        Users.insertAndGetId {
            it[name] = user.name
            it[age] = user.age
            it[userName] = user.userName
            it[password] = PasswordUtils.hashPassword(user.password)
            it[email] = user.email
        }.value
    }

    fun read(email: String): User? {
        return transaction {
            Users.select { Users.email eq email }
                .map { User(it[Users.id].value, it[Users.name], it[Users.age], it[Users.userName], "*******", it[Users.email]) }
                .singleOrNull()
        }
    }

    fun find(email: String, password: String): Boolean {
        return transaction {
            try {
                val saved = Users.slice(Users.password).select { Users.email eq email }.map { it[Users.password] }.first()
                return@transaction PasswordUtils.checkPassword(password, saved)
            } catch (ex: NoSuchElementException) {
                return@transaction false
            }
        }
    }

    fun update(email: String, user: User) {
        transaction {
            Users.update({ Users.email eq email }) {
                it[name] = user.name
                it[age] = user.age
                it[userName] = user.userName
            }
        }
    }

    fun delete(email: String) {
        transaction {
            Users.deleteWhere { Users.email.eq(email) }
        }
    }

    fun getUserExpenses(email: String): List<Expense> {
        return transaction {
            val appUser = AppUser.find { Users.email eq email }.first()
            return@transaction appUser.expenses.map {
                Expense(it.id.value, it.description, it.amount, it.category, it.place, it.time)
            }
        }
    }
}

fun getUserByEmail(userEmail: String): Query = Users.select { Users.email eq userEmail }
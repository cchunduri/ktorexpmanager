package com.cchunduri.plugins

import com.cchunduri.dao.AppUser
import com.cchunduri.dao.Users
import com.cchunduri.utils.PasswordUtils
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class UserService(database: Database) {

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    fun create(user: AppUser): UUID? = transaction {
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

    fun read(email: String): AppUser? {
        return transaction {
            Users.select { Users.email eq email }
                .map { AppUser(it[Users.id].value, it[Users.name], it[Users.age], it[Users.userName], "*******", it[Users.email]) }
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

    fun update(email: String, user: AppUser) {
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
}

fun getUserByEmail(userEmail: String): Query = Users.select { Users.email eq userEmail }
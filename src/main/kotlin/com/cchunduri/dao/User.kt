package com.cchunduri.dao

import com.cchunduri.serailizers.UUIDSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = UUID.randomUUID(),
    val name: String = "", val age: Int = 0,
    val userName: String = "",
    val password: String = "",
    val email: String = "",
    val expenses: List<Expense> = listOf()
)

object Users : UUIDTable() {
    val name = varchar("name", length = 50)
    val age = integer("age")
    val userName = varchar("user-name", length = 50)
    val password = varchar("password", length = 255)
    val email = varchar("email", length = 60)
}

class AppUser(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<AppUser>(Users)
    var name by Users.name
    val age by Users.age
    val userName by Users.userName
    val password by Users.password
    val email by Users.email
    val expenses by UserExpenses referrersOn Expenses.userId
}
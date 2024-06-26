package com.cchunduri.dao

import com.cchunduri.serailizers.LocalDateSerializer
import com.cchunduri.serailizers.UUIDSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class Expense(
    @Serializable(with = UUIDSerializer::class) val id: UUID? = null,
    val description: String,
    val amount: Double,
    val category: String,
    val place: String,
    @Serializable(with = LocalDateSerializer::class)
    val time: LocalDateTime? = null
)

object Expenses : UUIDTable() {
    val description = text("description")
    val amount = double("amount")
    val category = varchar("category", 60)
    val place = varchar("place", 60)
    val time = datetime("time")
    val userId = reference("userId", Users)
}

class UserExpenses(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<UserExpenses>(Expenses)
    val description by Expenses.description
    val amount by Expenses.amount
    val category by Expenses.category
    val place by Expenses.place
    val time by Expenses.time
    val user by AppUser referencedOn Expenses.userId
}
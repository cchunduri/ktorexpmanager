package com.cchunduri.dao

import com.cchunduri.serailizers.UUIDSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import java.util.UUID

@Serializable
data class AppUser(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = UUID.randomUUID(),
    val name: String, val age: Int,
    val userName: String,
    val password: String,
    val email: String
)

object Users : UUIDTable() {
    val name = varchar("name", length = 50)
    val age = integer("age")
    val userName = varchar("user-name", length = 50)
    val password = varchar("password", length = 255)
    val email = varchar("email", length = 60)
}
package com.cchunduri.dao

import com.cchunduri.serailizers.LocalDateSerializer
import com.cchunduri.serailizers.UUIDSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class Expense(
    @Serializable(with = UUIDSerializer::class) val id: UUID? = null,
    val description: String, val amount: Double, val category: String, val place: String,
    @Serializable(with = LocalDateSerializer::class) val time: LocalDateTime? = null
)

object Expenses : UUIDTable() {
    val description = text("description")
    val amount = double("amount")
    val category = varchar("category", 60)
    val place = varchar("place", 60)
    val time = datetime("time")
    val userId = reference("id", Users)
}





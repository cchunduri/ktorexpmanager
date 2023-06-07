package com.cchunduri.dto

import com.cchunduri.serailizers.LocalDateSerializer
import com.cchunduri.serailizers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class Expense(
    @Serializable(with = UUIDSerializer::class)  val id: UUID,
    val description: String, val amount: Double, val category: String, val place: String,
    @Serializable(with = LocalDateSerializer::class) val time: LocalDateTime
)
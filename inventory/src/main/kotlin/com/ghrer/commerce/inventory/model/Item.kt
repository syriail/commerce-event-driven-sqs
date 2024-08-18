package com.ghrer.commerce.inventory.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
data class Item(
    @Id
    val id: UUID,
    val displayName: String,
    val description: String,
    val quantity: Int,
    val reserved: Int = 0,
    val price: Double,
    val onShelf: Int,
)

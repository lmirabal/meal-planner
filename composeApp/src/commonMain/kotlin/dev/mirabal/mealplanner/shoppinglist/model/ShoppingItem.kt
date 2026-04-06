package dev.mirabal.mealplanner.shoppinglist.model

import kotlin.time.Instant
import kotlin.uuid.Uuid

value class ItemId(val value: Uuid)
value class ItemName(val value: String) {
    init {
        require(value.isNotBlank())
        require(value == value.trim())
    }
}
value class ListId(val value: Uuid)
value class CreatedAt(val value: Instant)
value class UpdatedAt(val value: Instant)

data class ShoppingItem(
    val id: ItemId,
    val listId: ListId,
    val name: ItemName,
    val checked: Boolean,
    val createdAt: CreatedAt,
    val updatedAt: UpdatedAt,
) {
    companion object {
        val DEFAULT_LIST_ID = ListId(Uuid.parse("00000000-0000-0000-0000-000000000001"))
    }
}

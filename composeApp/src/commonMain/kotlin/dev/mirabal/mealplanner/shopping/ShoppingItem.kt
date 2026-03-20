package dev.mirabal.mealplanner.shopping

data class ShoppingItem(
    val id: ItemId,
    val listId: ListId,
    val name: ItemName,
    val checked: Boolean,
    val addedAt: AddedAt,
    val checkedAt: CheckedAt?,
    val updatedAt: UpdatedAt,
)

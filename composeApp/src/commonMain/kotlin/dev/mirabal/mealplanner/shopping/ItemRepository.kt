package dev.mirabal.mealplanner.shopping

import kotlinx.coroutines.flow.Flow

internal interface ItemRepository {
    fun observeItems(): Flow<List<ShoppingItem>>
    suspend fun addItem(name: ItemName)
}

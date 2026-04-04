package dev.mirabal.mealplanner.shoppinglist

import dev.mirabal.mealplanner.shoppinglist.model.ItemName
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getAll(): Flow<List<ShoppingItem>>
    suspend fun add(name: ItemName)
}

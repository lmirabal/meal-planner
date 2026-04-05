package dev.mirabal.mealplanner.shoppinglist

import dev.mirabal.mealplanner.shoppinglist.model.ItemName
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem
import kotlin.time.Clock
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getAll(): Flow<List<ShoppingItem>>
    suspend fun add(name: ItemName)
}

fun itemRepository(clock: Clock = Clock.System): ItemRepository = InMemoryItemRepository(clock)

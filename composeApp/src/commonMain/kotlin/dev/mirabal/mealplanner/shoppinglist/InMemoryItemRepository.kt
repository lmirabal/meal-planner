package dev.mirabal.mealplanner.shoppinglist

import dev.mirabal.mealplanner.shoppinglist.model.CreatedAt
import dev.mirabal.mealplanner.shoppinglist.model.ItemId
import dev.mirabal.mealplanner.shoppinglist.model.ItemName
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem.Companion.DEFAULT_LIST_ID
import dev.mirabal.mealplanner.shoppinglist.model.UpdatedAt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Clock
import kotlin.uuid.Uuid

internal class InMemoryItemRepository(
    private val clock: Clock = Clock.System,
) : ItemRepository {

    private val items = MutableStateFlow<List<ShoppingItem>>(emptyList())

    override fun getAll(): Flow<List<ShoppingItem>> = items.asStateFlow()

    override suspend fun add(name: ItemName) {
        val now = clock.now()
        val item = ShoppingItem(
            id = ItemId(Uuid.random()),
            listId = DEFAULT_LIST_ID,
            name = name,
            checked = false,
            createdAt = CreatedAt(now),
            updatedAt = UpdatedAt(now),
        )
        items.update { listOf(item) + it }
    }
}

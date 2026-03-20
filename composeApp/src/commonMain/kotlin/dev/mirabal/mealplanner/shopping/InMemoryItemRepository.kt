package dev.mirabal.mealplanner.shopping

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class InMemoryItemRepository : ItemRepository {
    private val _items = MutableStateFlow<List<ShoppingItem>>(emptyList())

    override fun observeItems(): Flow<List<ShoppingItem>> = _items.asStateFlow()

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun addItem(name: ItemName) {
        val now = Clock.System.now()
        val item = ShoppingItem(
            id = ItemId(Uuid.random().toString()),
            listId = ListId("default"),
            name = name,
            checked = false,
            addedAt = AddedAt(now),
            checkedAt = null,
            updatedAt = UpdatedAt(now),
        )
        _items.update { listOf(item) + it }
    }
}

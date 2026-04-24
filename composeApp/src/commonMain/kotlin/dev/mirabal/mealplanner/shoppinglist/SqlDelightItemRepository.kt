package dev.mirabal.mealplanner.shoppinglist

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.mirabal.mealplanner.db.ShoppingDatabase
import dev.mirabal.mealplanner.shoppinglist.model.CreatedAt
import dev.mirabal.mealplanner.shoppinglist.model.ItemId
import dev.mirabal.mealplanner.shoppinglist.model.ItemName
import dev.mirabal.mealplanner.shoppinglist.model.ListId
import dev.mirabal.mealplanner.shoppinglist.model.Quantity
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem.Companion.DEFAULT_LIST_ID
import dev.mirabal.mealplanner.shoppinglist.model.UpdatedAt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid
import dev.mirabal.mealplanner.db.ShoppingItem as DbShoppingItem

internal class SqlDelightItemRepository(
    database: ShoppingDatabase,
    private val clock: Clock = Clock.System,
) : ItemRepository {

    private val queries = database.shoppingDatabaseQueries

    override fun getAll(): Flow<List<ShoppingItem>> =
        queries.selectAllItems()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbItems -> dbItems.map { it.toDomain() } }

    override suspend fun add(name: ItemName, quantity: Quantity?) {
        val now = clock.now()
        withContext(Dispatchers.IO) {
            queries.insertItem(
                id = Uuid.random().toString(),
                list_id = DEFAULT_LIST_ID.value.toString(),
                name = name.value,
                quantity = quantity?.serialize(),
                checked = 0L,
                created_at = now.toEpochMilliseconds(),
                updated_at = now.toEpochMilliseconds(),
            )
        }
    }
}

fun itemRepository(database: ShoppingDatabase): ItemRepository = SqlDelightItemRepository(database)

private fun DbShoppingItem.toDomain() = ShoppingItem(
    id = ItemId(Uuid.parse(id)),
    listId = ListId(Uuid.parse(list_id)),
    name = ItemName(name),
    quantity = quantity?.let { Quantity.parse(it) },
    checked = checked != 0L,
    createdAt = CreatedAt(Instant.fromEpochMilliseconds(created_at)),
    updatedAt = UpdatedAt(Instant.fromEpochMilliseconds(updated_at)),
)

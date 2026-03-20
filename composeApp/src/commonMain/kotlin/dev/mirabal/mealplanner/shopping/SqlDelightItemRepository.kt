package dev.mirabal.mealplanner.shopping

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.mirabal.mealplanner.MealPlannerDatabaseQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class SqlDelightItemRepository(
    private val queries: MealPlannerDatabaseQueries,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ItemRepository {

    override fun observeItems(): Flow<List<ShoppingItem>> =
        queries.selectAllItems()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { rows -> rows.map { it.toDomain() } }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun addItem(name: ItemName) {
        val now = Clock.System.now()
        withContext(ioDispatcher) {
            queries.insertItem(
                id = Uuid.random().toString(),
                list_id = "default",
                name = name.value,
                checked = 0L,
                created_at = now.toEpochMilliseconds(),
                updated_at = now.toEpochMilliseconds(),
            )
        }
    }
}

private fun dev.mirabal.mealplanner.ShoppingItem.toDomain() = ShoppingItem(
    id = ItemId(id),
    listId = ListId(list_id),
    name = ItemName(name),
    checked = checked == 1L,
    addedAt = AddedAt(Instant.fromEpochMilliseconds(created_at)),
    checkedAt = null,
    updatedAt = UpdatedAt(Instant.fromEpochMilliseconds(updated_at)),
)

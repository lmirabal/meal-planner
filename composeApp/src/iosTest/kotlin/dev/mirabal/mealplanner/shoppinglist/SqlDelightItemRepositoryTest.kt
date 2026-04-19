package dev.mirabal.mealplanner.shoppinglist

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import dev.mirabal.mealplanner.db.ShoppingDatabase
import dev.mirabal.mealplanner.shoppinglist.model.CreatedAt
import dev.mirabal.mealplanner.shoppinglist.model.ItemName
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem.Companion.DEFAULT_LIST_ID
import dev.mirabal.mealplanner.shoppinglist.model.UpdatedAt
import dev.mirabal.mealplanner.shoppinglist.testutil.FakeClock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class SqlDelightItemRepositoryTest {

    private val clock = FakeClock(Instant.fromEpochMilliseconds(1_000_000))

    private fun createRepository(): SqlDelightItemRepository {
        val schema = ShoppingDatabase.Schema
        val driver = NativeSqliteDriver(
            DatabaseConfiguration(
                name = "test-${Uuid.random()}.db",
                version = schema.version.toInt(),
                create = { connection ->
                    wrapConnection(connection) { schema.create(it) }
                },
                upgrade = { connection, oldVersion, newVersion ->
                    wrapConnection(connection) {
                        schema.migrate(it, oldVersion.toLong(), newVersion.toLong())
                    }
                },
                inMemory = true,
            )
        )
        return SqlDelightItemRepository(ShoppingDatabase(driver), clock)
    }

    @Test
    fun initiallyEmitsEmptyList() = runTest {
        val repository = createRepository()
        assertEquals(emptyList(), repository.getAll().first())
    }

    @Test
    fun addEmitsItemWithCorrectFields() = runTest {
        val repository = createRepository()
        repository.add(ItemName("Milk"))

        val item = repository.getAll().first().first()
        assertEquals(
            ShoppingItem(
                id = item.id,
                listId = DEFAULT_LIST_ID,
                name = ItemName("Milk"),
                checked = false,
                createdAt = CreatedAt(clock.now),
                updatedAt = UpdatedAt(clock.now),
            ),
            item,
        )
    }

    @Test
    fun multipleAddsAreNewestFirst() = runTest {
        val repository = createRepository()
        repository.add(ItemName("Eggs"))
        clock.now = Instant.fromEpochMilliseconds(2_000_000)
        repository.add(ItemName("Butter"))

        val names = repository.getAll().first().map { it.name.value }
        assertEquals(listOf("Butter", "Eggs"), names)
    }
}

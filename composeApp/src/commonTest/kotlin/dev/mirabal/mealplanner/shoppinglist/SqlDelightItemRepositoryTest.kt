package dev.mirabal.mealplanner.shoppinglist

import dev.mirabal.mealplanner.db.ShoppingDatabase
import dev.mirabal.mealplanner.shoppinglist.model.CreatedAt
import dev.mirabal.mealplanner.shoppinglist.model.ItemName
import dev.mirabal.mealplanner.shoppinglist.model.Quantity
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem.Companion.DEFAULT_LIST_ID
import dev.mirabal.mealplanner.shoppinglist.model.UpdatedAt
import dev.mirabal.mealplanner.shoppinglist.testutil.FakeClock
import dev.mirabal.mealplanner.shoppinglist.testutil.createTestSqlDriver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class SqlDelightItemRepositoryTest {

    private val clock = FakeClock(Instant.fromEpochMilliseconds(1_000_000))

    private fun createRepository(): SqlDelightItemRepository {
        val driver = createTestSqlDriver(ShoppingDatabase.Schema)
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
                quantity = null,
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

    @Test
    fun addWithQuantityPersistsAndRetrievesQuantity() = runTest {
        val repository = createRepository()
        repository.add(ItemName("Lemons"), Quantity.WholeNumber(3))

        val item = repository.getAll().first().first()
        assertEquals(Quantity.WholeNumber(3), item.quantity)
    }
}

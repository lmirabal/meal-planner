package dev.mirabal.mealplanner.shoppinglist

import dev.mirabal.mealplanner.shoppinglist.model.CreatedAt
import dev.mirabal.mealplanner.shoppinglist.model.ItemName
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem.Companion.DEFAULT_LIST_ID
import dev.mirabal.mealplanner.shoppinglist.model.UpdatedAt
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class InMemoryItemRepositoryTest {

    private class FakeClock(var now: Instant) : Clock {
        override fun now(): Instant = now
    }

    private val clock = FakeClock(Instant.fromEpochMilliseconds(1_000_000))
    private val repository = InMemoryItemRepository(clock)

    @Test
    fun initiallyEmitsEmptyList() = runTest {
        assertEquals(emptyList(), repository.getAll().first())
    }

    @Test
    fun addEmitsItemWithCorrectFields() = runTest {
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
        repository.add(ItemName("Eggs"))
        repository.add(ItemName("Butter"))

        val names = repository.getAll().first().map { it.name.value }
        assertEquals(listOf("Butter", "Eggs"), names)
    }

    @Test
    fun blankNameThrows() {
        assertFailsWith<IllegalArgumentException> { ItemName("") }
    }

    @Test
    fun whitespaceOnlyNameThrows() {
        assertFailsWith<IllegalArgumentException> { ItemName("  ") }
    }
}

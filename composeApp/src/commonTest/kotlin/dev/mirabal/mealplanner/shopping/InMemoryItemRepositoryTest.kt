package dev.mirabal.mealplanner.shopping

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class InMemoryItemRepositoryTest {

    private val repo = InMemoryItemRepository()

    @Test
    fun addedItemAppearsInObservedList() = runTest {
        repo.addItem(ItemName("Milk"))
        val items = repo.observeItems().first()
        assertEquals(1, items.size)
        assertEquals(ItemName("Milk"), items[0].name)
    }

    @Test
    fun multipleItemsAreOrderedNewestFirst() = runTest {
        repo.addItem(ItemName("Milk"))
        repo.addItem(ItemName("Eggs"))
        val items = repo.observeItems().first()
        assertEquals(ItemName("Eggs"), items[0].name)
        assertEquals(ItemName("Milk"), items[1].name)
    }

    @Test
    fun addedItemIsUncheckedByDefault() = runTest {
        repo.addItem(ItemName("Bread"))
        assertEquals(false, repo.observeItems().first()[0].checked)
    }

    @Test
    fun eachItemHasUniqueId() = runTest {
        repo.addItem(ItemName("Milk"))
        repo.addItem(ItemName("Eggs"))
        val items = repo.observeItems().first()
        assertNotEquals(items[0].id, items[1].id)
    }
}

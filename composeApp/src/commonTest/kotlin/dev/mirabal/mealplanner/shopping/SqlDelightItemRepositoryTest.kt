package dev.mirabal.mealplanner.shopping

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import dev.mirabal.mealplanner.MealPlannerDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class SqlDelightItemRepositoryTest {

    private val driver = NativeSqliteDriver(MealPlannerDatabase.Schema, ":memory:")
    private lateinit var repo: SqlDelightItemRepository

    @BeforeTest
    fun setUp() {
        driver.execute(null, "DELETE FROM ShoppingItem", 0)
        repo = SqlDelightItemRepository(
            queries = MealPlannerDatabase(driver).mealPlannerDatabaseQueries,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

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

    @Test
    fun secondRepositoryInstanceSeesPersistedItems() = runTest {
        repo.addItem(ItemName("Butter"))
        val secondRepo = SqlDelightItemRepository(
            queries = MealPlannerDatabase(driver).mealPlannerDatabaseQueries,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
        val items = secondRepo.observeItems().first()
        assertEquals(1, items.size)
        assertEquals(ItemName("Butter"), items[0].name)
    }
}

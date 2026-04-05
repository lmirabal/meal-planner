package dev.mirabal.mealplanner.shoppinglist

import dev.mirabal.mealplanner.shoppinglist.model.CreatedAt
import dev.mirabal.mealplanner.shoppinglist.model.ItemName
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem.Companion.DEFAULT_LIST_ID
import dev.mirabal.mealplanner.shoppinglist.model.UpdatedAt
import dev.mirabal.mealplanner.shoppinglist.testutil.FakeClock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val clock = FakeClock(Instant.fromEpochMilliseconds(1_000_000))
    private val repository = InMemoryItemRepository(clock)
    private lateinit var viewModel: ShoppingListViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ShoppingListViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateIsEmpty() = runTest {
        assertEquals(emptyList(), viewModel.items.value)
        assertEquals("", viewModel.inputText.value)
    }

    @Test
    fun onInputChangedUpdatesInputText() = runTest {
        viewModel.onInputChanged("Milk")
        assertEquals("Milk", viewModel.inputText.value)
    }

    @Test
    fun onAddClickedAddsItemAndClearsInput() = runTest(testDispatcher) {
        viewModel.items.launchIn(backgroundScope)
        viewModel.onInputChanged("  Milk  ")
        viewModel.onAddClicked()

        val item = viewModel.items.value.first()
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
        assertEquals("", viewModel.inputText.value)
    }

    @Test
    fun onAddClickedWithBlankInputIsNoOp() = runTest {
        viewModel.onInputChanged("   ")
        viewModel.onAddClicked()
        assertEquals(emptyList(), viewModel.items.value)
    }

    @Test
    fun multipleAddsAreNewestFirst() = runTest(testDispatcher) {
        viewModel.items.launchIn(backgroundScope)
        viewModel.onInputChanged("Eggs")
        viewModel.onAddClicked()
        viewModel.onInputChanged("Butter")
        viewModel.onAddClicked()

        val names = viewModel.items.value.map { it.name.value }
        assertEquals(listOf("Butter", "Eggs"), names)
    }
}

package dev.mirabal.mealplanner.shoppinglist

import dev.mirabal.mealplanner.shoppinglist.model.ItemName
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
        assertEquals(ShoppingListUiItem(id = item.id, label = "Milk"), item)
        assertEquals("", viewModel.inputText.value)
        assertEquals("", viewModel.quantityInput.value)
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

        val labels = viewModel.items.value.map { it.label }
        assertEquals(listOf("Butter", "Eggs"), labels)
    }

    @Test
    fun initialCanAddIsFalse() = runTest(testDispatcher) {
        viewModel.canAdd.launchIn(backgroundScope)
        assertEquals(false, viewModel.canAdd.value)
    }

    @Test
    fun canAddIsTrueWhenNameFilledAndQuantityBlank() = runTest(testDispatcher) {
        viewModel.canAdd.launchIn(backgroundScope)
        viewModel.onInputChanged("Milk")
        assertEquals(true, viewModel.canAdd.value)
    }

    @Test
    fun canAddIsFalseWhenQuantityInputIsInvalid() = runTest(testDispatcher) {
        viewModel.canAdd.launchIn(backgroundScope)
        viewModel.onInputChanged("Milk")
        viewModel.onQuantityChanged("abc")
        assertEquals(false, viewModel.canAdd.value)
    }

    @Test
    fun canAddIsTrueWhenNameFilledAndQuantityValid() = runTest(testDispatcher) {
        viewModel.canAdd.launchIn(backgroundScope)
        viewModel.onInputChanged("Lemons")
        viewModel.onQuantityChanged("3")
        assertEquals(true, viewModel.canAdd.value)
    }

    @Test
    fun onAddClickedWithQuantityStoresQuantityAndClearsFields() = runTest(testDispatcher) {
        viewModel.items.launchIn(backgroundScope)
        viewModel.onInputChanged("Lemons")
        viewModel.onQuantityChanged("3")
        viewModel.onAddClicked()

        val item = viewModel.items.value.first()
        assertEquals(ShoppingListUiItem(id = item.id, label = "3 Lemons"), item)
        assertEquals("", viewModel.inputText.value)
        assertEquals("", viewModel.quantityInput.value)
    }

    @Test
    fun onAddClickedWithInvalidQuantityIsNoOp() = runTest(testDispatcher) {
        viewModel.items.launchIn(backgroundScope)
        viewModel.onInputChanged("Lemons")
        viewModel.onQuantityChanged("abc")
        viewModel.onAddClicked()

        assertEquals(emptyList(), viewModel.items.value)
    }
}

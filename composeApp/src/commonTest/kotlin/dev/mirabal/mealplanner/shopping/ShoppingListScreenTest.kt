package dev.mirabal.mealplanner.shopping

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ShoppingListScreenTest {

    @Test
    fun typingAndTappingAddShowsItemInList() = runComposeUiTest {
        val viewModel = ShoppingListViewModel(InMemoryItemRepository())
        setContent { MaterialTheme { ShoppingListScreen(viewModel) } }

        onNodeWithText("Item name").performTextInput("Milk")
        onNodeWithText("Add").performClick()

        onNodeWithText("Milk").assertIsDisplayed()
    }

    @Test
    fun afterAddingItemInputFieldIsCleared() = runComposeUiTest {
        val viewModel = ShoppingListViewModel(InMemoryItemRepository())
        setContent { MaterialTheme { ShoppingListScreen(viewModel) } }

        onNodeWithText("Item name").performTextInput("Milk")
        onNodeWithText("Add").performClick()

        onNode(hasSetTextAction() and hasText("", substring = false)).assertExists()
    }

    @Test
    fun emptyInputDoesNotAddItem() = runComposeUiTest {
        val viewModel = ShoppingListViewModel(InMemoryItemRepository())
        setContent { MaterialTheme { ShoppingListScreen(viewModel) } }

        onNodeWithText("Add").performClick()

        assertEquals(0, viewModel.items.value.size)
    }
}

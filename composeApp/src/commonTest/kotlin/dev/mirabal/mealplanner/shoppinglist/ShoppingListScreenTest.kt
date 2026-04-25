package dev.mirabal.mealplanner.shoppinglist

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ShoppingListScreenTest {

    private val viewModel = ShoppingListViewModel(InMemoryItemRepository())

    @Test
    fun showsEmptyStateInitially() = runComposeUiTest {
        setContent {
            MaterialTheme {
                ShoppingListScreen(viewModel)
            }
        }

        onNodeWithText("Shopping List").assertExists()
        onNodeWithText("Add").assertExists()
    }

    @Test
    fun addButtonDisabledWhenInputEmpty() = runComposeUiTest {
        setContent {
            MaterialTheme {
                ShoppingListScreen(viewModel)
            }
        }

        onNodeWithText("Add").assertIsNotEnabled()
    }

    @Test
    fun typeAndTapAddShowsItemInList() = runComposeUiTest {
        setContent {
            MaterialTheme {
                ShoppingListScreen(viewModel)
            }
        }

        onNodeWithText("Item name").performTextInput("Milk")
        onNodeWithText("Add").assertIsEnabled()
        onNodeWithText("Add").performClick()
        onNodeWithText("Milk").assertExists()
    }

    @Test
    fun typeNameAndQuantityShowsFormattedItem() = runComposeUiTest {
        setContent {
            MaterialTheme {
                ShoppingListScreen(viewModel)
            }
        }

        onNodeWithText("Item name").performTextInput("Lemons")
        onNodeWithText("Qty").performTextInput("3")
        onNodeWithText("Add").assertIsEnabled()
        onNodeWithText("Add").performClick()
        onNodeWithText("3 Lemons").assertExists()
    }

    @Test
    fun invalidQuantityDisablesAddButton() = runComposeUiTest {
        setContent {
            MaterialTheme {
                ShoppingListScreen(viewModel)
            }
        }

        onNodeWithText("Item name").performTextInput("Lemons")
        onNodeWithText("Qty").performTextInput("abc")
        onNodeWithText("Add").assertIsNotEnabled()
    }
}

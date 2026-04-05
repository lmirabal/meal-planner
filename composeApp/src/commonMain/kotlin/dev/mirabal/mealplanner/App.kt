package dev.mirabal.mealplanner

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.mirabal.mealplanner.shoppinglist.ShoppingListViewModel
import dev.mirabal.mealplanner.shoppinglist.ShoppingListScreen

@Composable
fun App(appDependencies: AppDependencies) {
    MaterialTheme {
        val viewModel = viewModel { ShoppingListViewModel(appDependencies.itemRepository) }
        ShoppingListScreen(viewModel)
    }
}
